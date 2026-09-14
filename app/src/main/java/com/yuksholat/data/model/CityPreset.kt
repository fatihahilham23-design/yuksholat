package com.yuksholat.data.model

data class CityPreset(
    val id: String,
    val cityName: String,
    val provinceOrCountry: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Double
) {
    companion object {
        val INDONESIAN_CITIES = listOf(
            CityPreset("JJT", "Jakarta", "DKI Jakarta", -6.2088, 106.8456, 7.0),
            CityPreset("BDG", "Bandung", "Jawa Barat", -6.9175, 107.6191, 7.0),
            CityPreset("SBY", "Surabaya", "Jawa Timur", -7.2575, 112.7521, 7.0),
            CityPreset("SMG", "Semarang", "Jawa Tengah", -6.9667, 110.4167, 7.0),
            CityPreset("YGY", "Yogyakarta", "DI Yogyakarta", -7.7956, 110.3695, 7.0),
            CityPreset("MDN", "Medan", "Sumatera Utara", 3.5952, 98.6722, 7.0),
            CityPreset("PLB", "Palembang", "Sumatera Selatan", -2.9761, 104.7754, 7.0),
            CityPreset("PDG", "Padang", "Sumatera Barat", -0.9471, 100.4172, 7.0),
            CityPreset("ACH", "Banda Aceh", "Aceh", 5.5483, 95.3238, 7.0),
            CityPreset("MKS", "Makassar", "Sulawesi Selatan", -5.1477, 119.4327, 8.0),
            CityPreset("DPS", "Denpasar", "Bali", -8.6705, 115.2126, 8.0),
            CityPreset("BJM", "Banjarmasin", "Kalimantan Selatan", -3.3194, 114.5908, 8.0),
            CityPreset("SMD", "Samarinda", "Kalimantan Timur", -0.5022, 117.1537, 8.0),
            CityPreset("PTK", "Pontianak", "Kalimantan Barat", -0.0263, 109.3425, 7.0),
            CityPreset("MTR", "Mataram", "Nusa Tenggara Barat", -8.5768, 116.1026, 8.0),
            CityPreset("KPR", "Kupang", "Nusa Tenggara Timur", -10.1772, 123.6070, 8.0),
            CityPreset("JPR", "Jayapura", "Papua", -2.5337, 140.7181, 9.0),
            CityPreset("AMB", "Ambon", "Maluku", -3.6954, 128.1814, 9.0),
            CityPreset("MND", "Manado", "Sulawesi Utara", 1.4748, 124.8421, 8.0),
            CityPreset("MKH", "Makkah", "Arab Saudi", 21.4225, 39.8262, 3.0),
            CityPreset("MDH", "Madinah", "Arab Saudi", 24.4672, 39.6111, 3.0)
        )

        val DEFAULT_CITY = INDONESIAN_CITIES[0]
    }
}