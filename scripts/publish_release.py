#!/usr/bin/env python3
import os
import re
import sys
import json
import urllib.request
import urllib.error
import subprocess
from pathlib import Path

def get_git_remote_info():
    try:
        url = subprocess.check_output(["git", "remote", "get-url", "origin"], stderr=subprocess.DEVNULL).decode().strip()
    except Exception as e:
        print(f"Hiba a git remote lekérdezésekor: {e}", file=sys.stderr)
        sys.exit(1)

    token = None
    repo_path = None
    if "@github.com" in url:
        token_part = url.split("://")[1].split("@github.com")[0]
        if ":" in token_part:
            token = token_part.split(":")[1]
        else:
            token = token_part
        repo_part = url.split("@github.com/")[1]
    elif "github.com/" in url:
        repo_part = url.split("github.com/")[1]
    else:
        print("Nem GitHub tároló URL.", file=sys.stderr)
        sys.exit(1)

    if repo_part.endswith(".git"):
        repo_part = repo_part[:-4]
    return token, repo_part

def get_version_from_gradle():
    gradle_file = Path("app/build.gradle.kts")
    if not gradle_file.exists():
        print("app/build.gradle.kts nem található.", file=sys.stderr)
        sys.exit(1)
    content = gradle_file.read_text(encoding="utf-8")
    match = re.search(r'versionName\s*=\s*"([^"]+)"', content)
    if not match:
        print("versionName nem található a build.gradle.kts-ben.", file=sys.stderr)
        sys.exit(1)
    return match.group(1).strip()

def find_apk():
    release_apk = Path("app/build/outputs/apk/release/app-release.apk")
    debug_apk = Path("app/build/outputs/apk/debug/app-debug.apk")
    if release_apk.exists():
        return release_apk
    if debug_apk.exists():
        return debug_apk
    return None

def main():
    token, repo = get_git_remote_info()
    if not token:
        token = os.environ.get("GITHUB_TOKEN")
    if not token:
        print("GitHub token nem található a git remote-ban vagy GITHUB_TOKEN környezeti változóban.", file=sys.stderr)
        sys.exit(1)

    version = get_version_from_gradle()
    tag = f"v{version}" if not version.startswith("v") else version
    release_title = tag
    
    apk_path = find_apk()
    if not apk_path:
        print("Nem található lefordított APK fájl az app/build/outputs/apk/ könyvtárban.", file=sys.stderr)
        sys.exit(1)

    print(f"Verzió: {version} -> Tag: {tag}")
    print(f"APK fájl: {apk_path} ({apk_path.stat().st_size / (1024*1024):.2f} MB)")

    # 1. Tag létrehozása és feltöltése a gitbe
    existing_tags = subprocess.check_output(["git", "tag", "-l", tag]).decode().strip()
    if not existing_tags:
        print(f"Helyi git tag létrehozása: {tag}...")
        subprocess.check_call(["git", "tag", "-a", tag, "-m", f"Release {tag}"])
    else:
        print(f"A(z) {tag} tag már létezik helyben.")

    print(f"Tag pusholása a távoli tárba...")
    try:
        subprocess.check_call(["git", "push", "origin", tag])
    except subprocess.CalledProcessError as e:
        print(f"Figyelem: git push origin {tag} figyelmeztetéssel fejeződött be (lehet, hogy már létezik távolin).")

    # 2. Release lekérése vagy létrehozása GitHub API-n
    api_headers = {
        "Authorization": f"token {token}",
        "Accept": "application/vnd.github+json",
        "User-Agent": "BPJournal-Release-Script"
    }

    # Ellenőrizzük, van-e már release
    get_release_url = f"https://api.github.com/repos/{repo}/releases/tags/{tag}"
    req = urllib.request.Request(get_release_url, headers=api_headers)
    release_data = None
    try:
        with urllib.request.urlopen(req) as resp:
            release_data = json.loads(resp.read().decode())
            print(f"Meglévő release megtalálva (id: {release_data['id']}).")
    except urllib.error.HTTPError as e:
        if e.code == 404:
            print(f"Új release létrehozása a GitHubon: {tag}...")
            create_url = f"https://api.github.com/repos/{repo}/releases"
            payload = {
                "tag_name": tag,
                "target_commitish": "main",
                "name": release_title,
                "body": f"BPJournal {tag} automatikus kiadás.",
                "draft": False,
                "prerelease": False
            }
            create_req = urllib.request.Request(
                create_url,
                data=json.dumps(payload).encode("utf-8"),
                headers={**api_headers, "Content-Type": "application/json"}
            )
            with urllib.request.urlopen(create_req) as resp:
                release_data = json.loads(resp.read().decode())
                print(f"Release sikeresen létrehozva (id: {release_data['id']}).")
        else:
            print(f"Hiba a release lekérésekor: HTTP {e.code}", file=sys.stderr)
            sys.exit(1)

    release_id = release_data["id"]
    upload_url_template = release_data["upload_url"] # pl. https://uploads.github.com/repos/.../releases/123/assets{?name,label}
    upload_base = upload_url_template.split("{")[0]

    asset_name = f"BPJournal-{tag}.apk"

    # Ha már van ilyen asset, töröljük először
    if "assets" in release_data:
        for asset in release_data["assets"]:
            if asset["name"] == asset_name:
                print(f"Korábbi asset ({asset_name}) törlése...")
                del_url = f"https://api.github.com/repos/{repo}/releases/assets/{asset['id']}"
                del_req = urllib.request.Request(del_url, headers=api_headers, method="DELETE")
                with urllib.request.urlopen(del_req) as del_resp:
                    pass

    # 3. APK bináris feltöltése
    print(f"APK feltöltése ({asset_name})...")
    upload_url = f"{upload_base}?name={asset_name}"
    apk_bytes = apk_path.read_bytes()

    upload_req = urllib.request.Request(
        upload_url,
        data=apk_bytes,
        headers={
            **api_headers,
            "Content-Type": "application/vnd.android.package-archive",
            "Content-Length": str(len(apk_bytes))
        }
    )

    with urllib.request.urlopen(upload_req) as resp:
        asset_data = json.loads(resp.read().decode())
        print(f"APK sikeresen feltöltve! Letöltési link: {asset_data.get('browser_download_url')}")

    print(f"\nSikeres publikálás! Release URL: {release_data.get('html_url')}")

if __name__ == "__main__":
    main()
