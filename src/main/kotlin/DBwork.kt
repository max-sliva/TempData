import com.couchbase.lite.*


class DBwork {
    fun writeToDB(){

    }

    fun readFromDB(){

    }
}

fun main() {
    CouchbaseLite.init()
    println("Starting DB")
    val cfg = DatabaseConfiguration()
    var database = Database("mydb", cfg)
//    var mutableDoc = MutableDocument().setString("version", "2.0")
//                    .setString("type", "SDK")
//    database.save(mutableDoc)
//    mutableDoc = database.getDocument(mutableDoc.id)?.toMutable()!!.setString("language", "Kotlin")
//    database.save(mutableDoc)
//    val document = database.getDocument(mutableDoc.id)
//    println(String.format("Document ID :: %s", document?.id))
//    println(String.format("Learning :: %s:", document?.getString("language")))
//    var mutableDoc = MutableDocument().setString("version", "3.0")
//                    .setString("type", "SDK").setString("language", "Java")
//    database.save(mutableDoc)
    val listQuery = QueryBuilder.select(SelectResult.all())
        .from(DataSource.database(database))
        .where(Expression.property("type").equalTo(Expression.string("SDK")))
    println("DB: ")
    for (result in listQuery.execute().allResults()) {
        for (k in result.getDictionary(0)!!.keys) {
            println(k + " = " + result.getDictionary(0)!!.getString(k))
        }
        println()
        //String.format("Number of rows :: %n",
        // rs.size()));
    }

}