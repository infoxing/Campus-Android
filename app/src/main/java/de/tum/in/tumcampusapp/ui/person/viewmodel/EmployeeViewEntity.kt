package de.tum.`in`.tumcampusapp.ui.person.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.model.person.Employee
import de.tum.`in`.tumcampusapp.model.person.*

data class EmployeeViewEntity(
        val gender: Gender,
        val id: String,
        val name: String,
        val nameWithTitle: String,
        val surname: String,
        val businessContact: Contact?,
        val consultationHours: String,
        val email: String,
        val groups: List<Group>?,
        val image: Bitmap?,
        val privateContact: Contact?,
        val rooms: List<Room>?,
        val telSubstations: List<TelSubstation>?,
        val title: String,
        val raw: Employee
) {

    companion object {

        @JvmStatic
        fun create(context: Context, employee: Employee): EmployeeViewEntity {
            val resourceId = if (employee.gender == Gender.FEMALE) R.string.mrs else R.string.mr
            val salutation = context.getString(resourceId)
            val salutationWithName = "$salutation ${employee.name} ${employee.surname}"

            val nameWithTitle = if (employee.title.isBlank()) {
                salutationWithName
            } else {
                "$salutationWithName, ${employee.title}"
            }

            val imageAsBytes = Base64.decode(employee.imageData.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
            val image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)

            return EmployeeViewEntity(
                    employee.gender ?: Gender.UNKNOWN, employee.id, employee.name, nameWithTitle,
                    employee.surname, employee.businessContact, employee.consultationHours,
                    employee.email, employee.groupList?.groups, image, employee.privateContact,
                    employee.roomList?.rooms, employee.telSubstationList?.substations, employee.title, employee
            )
        }

    }

}
