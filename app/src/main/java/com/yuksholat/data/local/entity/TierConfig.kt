package com.yuksholat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tier_configs")
data class TierConfig(
    @PrimaryKey val tierLevel: Int, // 1 to 5
    val tierName: String,
    val minPoints: Int,
    val maxPoints: Int,
    val iconName: String,
    val description: String,
    val badgeColorHex: String
) {
    companion object {
        val DEFAULT_TIERS = listOf(
            TierConfig(1, "Pemula Shalat", 0, 300, "sword_wood", "Langkah awal menggapai istiqomah.", "#4CAF50"),
            TierConfig(2, "Prajurit Fajar", 300, 800, "shield_iron", "Mulai terbiasa shalat tepat waktu.", "#2196F3"),
            TierConfig(3, "Ksatria Masjid", 800, 1500, "sword_gold", "Kedisiplinan ibadah semakin teguh.", "#FF9800"),
            TierConfig(4, "Master Istiqomah", 1500, 3000, "crown_ruby", "Menjaga shalat 5 waktu tanpa putus.", "#E91E63"),
            TierConfig(5, "Legenda Iman", 3000, 999999, "crown_diamond", "Puncak ketaatan dan teladan sejati.", "#9C27B0")
        )
    }
}