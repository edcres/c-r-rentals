# c-r-rentals

Android App for the users to keep track of rented items using an image, among other data. (built mostly in kotlin)

---

### Tools and Skills used:

- MVVM architecture
- Material Design
- Modal Bottom Sheet
- Room SQLite Database
- Take a picture with the native camera app
- Store a picture in internal storage
- Current date
- Glide
- LiveData
  - Livedata Observers
  - Kotlin Flow
- Kotlin coroutines (for synchronous executions)
- RecyclerView
  - Drag and drop to change positions of the items and store the position in the database
  - Swipe to delete functionality and decoration

---

<img width=180 src="https://user-images.githubusercontent.com/79296181/183285528-196ec090-ff85-4a1c-886b-e43677df55f8.gif" />

- The app displays a list of rented items (Bikes, Chairs, Paddle Boards)
- Each item displays the item type, room number, time it was rented, and a picture of the item
  - Clicking on an item pops up a bottom sheet with more information on the rented item.
    - Can edit the item (and change the picture) and delete the item.
- Can change the position of the items in the list and the new position is stored in the database

---

<img width=180 src="https://user-images.githubusercontent.com/79296181/183285510-cbbb301a-f828-4c8b-8020-4ed4b40741f3.gif" />

- To add a new item click the Floating Action Button
- The bottom sheet pops up and the user is able to add information about the rental item and add it to the database
