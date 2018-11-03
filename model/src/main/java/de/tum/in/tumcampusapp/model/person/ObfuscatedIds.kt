package de.tum.`in`.tumcampusapp.model.person

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "obfuscated_ids")
data class ObfuscatedIds(
        @PropertyElement var studierende: String = "",
        @PropertyElement var bedienstete: String = "",
        @PropertyElement var extern: String = ""
)
