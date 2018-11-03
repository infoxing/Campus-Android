package de.tum.`in`.tumcampusapp.model.cafeteria

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

/**
 * New Location
 *
 * @param id        Location ID, e.g. 100
 * @param category  Location category, e.g. library, cafeteria, info
 * @param name      Location name, e.g. Studentenwerksbibliothek
 * @param address   Address, e.g. Arcisstr. 21
 * @param room      Room, e.g. MI 00.01.123
 * @param transport Transportation station name, e.g. U2 Königsplatz
 * @param hours     Opening hours, e.g. Mo–Fr 8–24
 * @param remark    Additional information, e.g. Tel: 089-11111
 * @param url       Location URL, e.g. http://stud.ub.uni-muenchen.de/
 */
@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class Location(
        @PrimaryKey
        var id: Int = -1,
        var category: String = "",
        var name: String = "",
        var address: String = "",
        var room: String = "",
        var transport: String = "",
        var hours: String = "",
        var remark: String = "",
        var url: String = ""
) {

    companion object {

        @JvmStatic fun fromCSVRow(row: Array<out String>): Location {
            return Location(
                    row[0].toInt(), row[1], row[2], row[3],
                    row[4], row[5], row[6], row[7], row[8]
            )
        }


    }

}