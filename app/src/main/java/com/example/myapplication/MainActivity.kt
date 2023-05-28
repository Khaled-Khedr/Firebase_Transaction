package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.classes.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPriority: EditText
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private lateinit var textViewData: TextView


    //private lateinit var listener:ListenerRegistration  //allows us to remove a listener when we remove the app
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()  //getting an instance of the db

    // private val docRf: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRf: CollectionReference =
        db.collection("Notebook") //this allows us to add new notes to it and its refers to a collection of documents
    private var lastResult: DocumentSnapshot? = null  //a document snapshot and its nullable

    //a document reference in our db so we don't keep on typing this over and over again
    private val KEY_TITLE = "title"  //for mutable maps
    private val KEY_DESCRIPTION = "description" //same as above
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        saveButton = findViewById(R.id.button_add_button)
        loadButton = findViewById(R.id.load_button)
        textViewData = findViewById(R.id.text_view_data)
        editTextPriority = findViewById(R.id.edit_text_priority)


        saveButton.setOnClickListener {
            addNote()
        }

        executeTransaction()
    }


    override fun onStart() {
        super.onStart()

        /* noteBookRf.orderBy("priority").addSnapshotListener(this) { snapshot, error ->
             error?.let {
                 return@addSnapshotListener
             }
             snapshot?.let {
                 for (dc in it.documentChanges) //basically we are looping to check changes in the documents
                 {
                     val id = dc.document.id
                     val oldIndex = dc.oldIndex
                     val newIndex = dc.newIndex

                     when (dc.type) {
                         DocumentChange.Type.ADDED -> {
                             textViewData.append(
                                 "\nAdded: $id" +
                                         "\nOld Index: $oldIndex New Index: $newIndex"
                             )
                         }

                         DocumentChange.Type.REMOVED -> {
                             textViewData.append(
                                 "\nRemoved: $id" +
                                         "\nOld Index: $oldIndex New Index: $newIndex"
                             )
                         }

                         DocumentChange.Type.MODIFIED -> {
                             textViewData.append(
                                 "\nModified: $id" +
                                         "\nOld Index: $oldIndex New Index: $newIndex"
                             )
                         }
                     }
                 }
             }
         }*/
    }


    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        /*  //THE FIRST METHOD MAP
            val note =
                mutableMapOf<String, Any>()  //in firebase data is stored in pairs so we need a mutable map
            note.put(KEY_TITLE, title)
            note.put(KEY_DESCRIPTION, description)

         */
        if (editTextPriority.text.toString().isEmpty()) {
            editTextPriority.setText("0")
        }
        val priority = editTextPriority.text.toString().toInt()
        val note = Note(title, description, priority)

        noteBookRf.add(note)  //adds a new document
            //or u can type docRf.set(note) since we made a reference
            //setting a collection name and a document name firebase can do it auto tho then setting it to the note map
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not added!", Toast.LENGTH_SHORT)
                    .show()

            }

    }

    /*
.addOnSuccessListener {  //we are basically looping through a query of document snapshots
        querydocumentsnapshots ->
    var data = " "  //used to append the data we get

    for (documentSnapshot in querydocumentsnapshots) {

        val note = documentSnapshot.toObject(Note::class.java)
        val title = note.title
        val description = note.description
        val priority = note.priority
        data += "Title: " + title + "\nDescription: $description" +
                "\nPriority: $priority \n\n"
    }
    textViewData.text = data
}.addOnFailureListener {
    Log.d(TAG,it.toString())  //the link to make the query online
}

     */

    private fun executeTransaction() {  //look in the db for the results
       db.runTransaction {
           val documentRef=noteBookRf.document("New note") //document reference to run our transaction on
           val snapshot=it.get(documentRef) //getting the document ref
           val newPriority=snapshot.getLong("priority")?.plus(1)  //to add one we need an elvis operator and .plus function to add 1
           it.update(documentRef,"priority",newPriority)

       }
    }
}




