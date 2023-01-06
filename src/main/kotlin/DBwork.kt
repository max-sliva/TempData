import com.couchbase.lite.*


class DBwork {
    var cfg: DatabaseConfiguration
    var database: Database
    var mutableDoc = MutableDocument()
    init {
        CouchbaseLite.init()
        println("Starting DB")
        cfg = DatabaseConfiguration()
        database = Database("mydb", cfg)
    }

    fun writeToDB(record:  HashMap<String, String>){

    }
    fun writeToDB(headers: Array<String>, records: ArrayList<List<String>>) {
        records.forEach {
            val jsonString = listToJSON(it, headers)
//            println(jsonString)
            mutableDoc = MutableDocument().setJSON(jsonString)
            database.save(mutableDoc)
//            println()
        }
    }

    private fun listToJSON(it: List<String>, headers: Array<String>): String {
        var i = 0
        var str = """{"""
        it.forEach { it1 ->
            str = str +""" "${headers[i]}": "$it1","""
//                    .setString("type", "SDK")

//                print("${headers[i]}: $it1 ")
            i++
        }
        str = str.replaceRange(str.length-1, str.length, "}")
        return str
    }

    fun matrixArrayToString(matrixArray: Array<String>): String{
        var str = """{"device": "matrix", "matrixType": "all", "matrixArray": ["""
        matrixArray.forEach { str +=  """"$it","""}
        str = str.replaceRange(str.length-1, str.length, "]}")

        return  str
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
//        .where(Expression.property("type").equalTo(Expression.string("SDK")))
//    println("DB: ${listQuery.}")
    var i = 0
    for (result in listQuery.execute().allResults()) {
        for (k in result.getDictionary(0)!!.keys) {
            print(k + " : " + result.getDictionary(0)!!.getString(k)+" ")
        }
        i++
        println()
    }
    println("db records = $i")

}