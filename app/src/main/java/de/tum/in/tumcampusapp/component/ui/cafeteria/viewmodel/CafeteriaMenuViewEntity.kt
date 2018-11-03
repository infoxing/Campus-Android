package de.tum.`in`.tumcampusapp.component.ui.cafeteria.viewmodel

import de.tum.`in`.tumcampusapp.model.cafeteria.CafeteriaMenu
import de.tum.`in`.tumcampusapp.model.cafeteria.MenuType
import java.util.regex.Pattern

data class CafeteriaMenuViewEntity(
        val id: Int,
        val cafeteriaId: Int,
        val name: String,
        val formattedName: String,
        val menuType: MenuType,
        val notificationTitle: String,
        val typeShort: String,
        val typeLong: String,
        val typeNumber: Int
) {

    companion object {

        private val REMOVE_PARENTHESES_PATTERN: Pattern = Pattern.compile("\\([^\\)]+\\)")
        private val REMOVE_DISH_ENUMERATION_PATTERN: Pattern = Pattern.compile("[0-9]")

        @JvmStatic
        fun create(cafeteriaMenu: CafeteriaMenu): CafeteriaMenuViewEntity {
            val formattedName = REMOVE_PARENTHESES_PATTERN.matcher(cafeteriaMenu.name).replaceAll("").trim()
            val notificationTitle = REMOVE_DISH_ENUMERATION_PATTERN
                    .matcher(cafeteriaMenu.typeLong)
                    .replaceAll("")
                    .trim()

            val menuType = when (cafeteriaMenu.typeShort) {
                "tg" -> MenuType.DAILY_SPECIAL
                "ae" -> MenuType.DISCOUNTED_COURSE
                "akt" -> MenuType.SPECIALS
                "bio" -> MenuType.BIO
                else -> MenuType.SIDE_DISH
            }

            return CafeteriaMenuViewEntity(
                    cafeteriaMenu.id, cafeteriaMenu.cafeteriaId,
                    cafeteriaMenu.name, formattedName, menuType,
                    notificationTitle, cafeteriaMenu.typeShort,
                    cafeteriaMenu.typeLong, cafeteriaMenu.typeNr
            )
        }

    }

}
