package com.kaszast.bpjournal.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Soft Slate & Teal Paletta (5. verzió és Clinical Dark hibrid - ZÉRÓ UV / ZÉRÓ LILA)

// Light Mode színek
val LightBg = Color(0xFFF3F6FA)              // Lágy felhőfehér háttér
val LightCardBg = Color(0xFFFFFFFF)          // Hófehér kártyák
val LightCardBorder = Color(0xFFE2E8F0)      // Finom kártyaszegély
val LightTextPrimary = Color(0xFF0F172A)     // Mély pala fekete
val LightTextSecondary = Color(0xFF64748B)   // Finom grafitszürke
val LightSystolic = Color(0xFF1E3A8A)        // Mélykék szisztolés vonal
val LightDiastolic = Color(0xFF10B981)       // Mentazöld diasztolés vonal
val LightTargetZone = Color(0xFFD1FAE5)      // Célzóna áttetsző zöld sáv
val LightPulseTeal = Color(0xFF0D9488)       // Pulzus gyűrű menta

// Dark Mode színek (Pontosan a mellékelt Clinical Dark kép szerint)
val DarkBg = Color(0xFF090D14)               // Mélyfekete háttér
val DarkCardBg = Color(0xFF131B29)           // Matt sötét pala kártya
val DarkCardBorder = Color(0xFF1E293B)       // Sötét szegély
val DarkTextPrimary = Color(0xFFF8FAFC)      // Hófehér tipográfia
val DarkTextSecondary = Color(0xFF94A3B8)    // Világosszürke felirat
val DarkSystolic = Color(0xFF38BDF8)         // Világoskék szisztolés görbe
val DarkDiastolic = Color(0xFF34D399)        // Élénk menta diasztolés görbe
val DarkTargetZone = Color(0xFF064E3B)       // Sötét célzóna sáv
val DarkPulseTeal = Color(0xFF14B8A6)        // Menta pulzus jelző

// Általános akcentusok
val AccentMint = Color(0xFF10B981)
val AccentTeal = Color(0xFF0D9488)
val AccentBlue = Color(0xFF2563EB)

// ESH Kategória Színek (Természetes orvosi paletta)
val CatOptimal = Color(0xFF10B981)           // Menta zöld
val CatNormal = Color(0xFF0D9488)            // Teal zöld
val CatHighNormal = Color(0xFFF59E0B)        // Meleg borostyán
val CatGrade1 = Color(0xFFF97316)            // Narancs
val CatGrade2 = Color(0xFFEF4444)            // Korall piros
val CatGrade3 = Color(0xFFB91C1C)            // Sötét vörös
val CatIsolated = Color(0xFFEA580C)          // Terrakotta

// Kompatibilitási aliasok
val SlatePrimary = AccentTeal
val TealSecondary = AccentMint
val CategoryOptimal = CatOptimal
val CategoryNormal = CatNormal
val CategoryHighNormal = CatHighNormal
val CategoryGrade1 = CatGrade1
val CategoryGrade2 = CatGrade2
val CategoryGrade3 = CatGrade3
val CategoryIsolated = CatIsolated
