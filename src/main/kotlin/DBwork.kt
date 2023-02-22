import com.couchbase.lite.*
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.json.JSONObject

class DBwork {
    var cfg: DatabaseConfiguration
    var database: Database
    var mutableDoc = MutableDocument()
    lateinit var dbRecords: List<Result>
    init {
        CouchbaseLite.init()
        println("Starting DB")
        cfg = DatabaseConfiguration()
        database = Database("mydb", cfg)
    }

    fun writeToDB(record:  HashMap<String, String>){

    }
    fun writeToDB(serialNumber: String, headers: Array<String>, records: ArrayList<List<String>>) {
        var i = 0
        var j = 0
        val recordsWithSerialNumber = getRecordsForSerialNumber(serialNumber)
        records.forEach {
//            println(jsonString)
            val jsonString = listToJSON(serialNumber, it, headers)
            if (!isRecordInDB(jsonString, recordsWithSerialNumber)){
                mutableDoc = MutableDocument().setJSON(jsonString)
                database.save(mutableDoc)
                i++
            }
            else j++
//            println()
        }
        println("added $i records, already in db $j records")
    }

    private fun isRecordInDB(jsonString: String, recordsWithSerialNumber: ObservableList<Map<String, StringProperty>>): Boolean {
        val jresponse = JSONObject(jsonString)
        val serialNumber = jresponse.getString("SerialNumber")
        val date = jresponse.getString("Date")
        val time = jresponse.getString("Time")
//        println("serial number = $serialNumber date = $date time = $time")
        for (record in recordsWithSerialNumber){
//            println("in db date = ${record["Date"]} time = ${record["Time"]}")
            if (record["Date"].toString().contains(date) && record["Time"].toString().contains(time)) {
//                println(record)
                //println("record already in db")
                return true
            }
        }
        return false
    }

    private fun listToJSON(serialNumber: String, it: List<String>, headers: Array<String>): String {
        var i = 0
        var str = """{"SerialNumber": "$serialNumber", """
        it.forEach { it1 ->
            str += """ "${headers[i]}": "$it1","""
//                    .setString("type", "SDK")

//                print("${headers[i]}: $it1 ")
            i++
        }
        str = str.replaceRange(str.length-1, str.length, "}")
        return str
    }

    fun dbSize(): Long {
//        var k = 0
//        val listQuery = QueryBuilder.select(SelectResult.all())
//            .from(DataSource.database(database))
//        k = listQuery.execute().count()
        return database.count
    }
    fun readFromDB(){

    }

    fun getDaysForMonth(month: String, year: String, serialNumber: String): MutableSet<String> {
        val daysSet = mutableSetOf<String>()
        val recordsWithMonth = getRecordsForMonthAndYear(serialNumber, year, month)
        println("recordsWithMonth count = ${recordsWithMonth.size}")
        for (record in recordsWithMonth){
            val temp = record["Date"].toString().split(".")[0].split(" ")
            daysSet.add(temp[temp.size-1])
        }
        println("days count = ${daysSet.size}")
        return daysSet
    }
    fun getMonthsForYear(year: String, serialNumber: String): MutableSet<String> {
        val monthsSet = mutableSetOf<String>()
        val recordsWithYear = getRecordsForYearAndSerialNumber(year, serialNumber)
        for (record in recordsWithYear){
            monthsSet.add(record["Date"].toString().split(".")[1])
        }
//        println()
        return monthsSet
    }

    fun getSerialNumbers(): MutableSet<String> {
        val listQuery = QueryBuilder.select(SelectResult.all())
                        .from(DataSource.database(database))
        val serialNumbersSet = mutableSetOf<String>()
        println("Loading from DB to dbRecords")
        dbRecords = listQuery.execute().allResults()
        for (record in dbRecords){
            serialNumbersSet.add(record.getDictionary(0)?.getString("SerialNumber")!!)
        }
        println("datesSet with serial numbers  = $serialNumbersSet")
        return serialNumbersSet
    }

    fun getYearsForSerialNumber(serialNumber: String): MutableSet<String> {
        val yearsSet = mutableSetOf<String>()
        val recordsWithSerialNumber = getRecordsForSerialNumber(serialNumber)
        for (result in recordsWithSerialNumber) {
            yearsSet.add(result["Date"].toString().split(".").last().dropLast(1))    //.takeLast(4)+"")
        }
        println("yearsSet = $yearsSet")
        return yearsSet
    }

    fun getRecordsForMonthAndYear(serialNumber: String, year: String, month: String): ObservableList<Map<String, StringProperty>>{
        val data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
        val recordsWithYear = getRecordsForYearAndSerialNumber(year, serialNumber)
        for (record in recordsWithYear){
            if (record["Date"].toString().split(".")[1].contains(month)) {
//                println(record)
                data.add(record)
            }
        }
        return data
    }

    fun getRecordsForSerialNumber(serialNumber: String): ObservableList<Map<String, StringProperty>>{
        println("results for $serialNumber")
        val data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
//        val listQuery = QueryBuilder.select(SelectResult.all())
//            .from(DataSource.database(database))
//        for (result in listQuery.execute().allResults()) {
        try {
           dbRecords.size
        } catch (e: UninitializedPropertyAccessException) {
            println("Loading from DB to dbRecords")
            val listQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
            dbRecords = listQuery.execute().allResults()
        }
        for (result in dbRecords) {
            val dataMap = mutableMapOf<String, StringProperty>()
            if (serialNumber!="0" && result.getDictionary(0)?.getString("SerialNumber")!!.contains(serialNumber)) {
                for (k in result.getDictionary(0)!!.keys) {
//                    print(k + " : " + result.getDictionary(0)!!.getString(k) + " ")
                    dataMap[k] = SimpleStringProperty(result.getDictionary(0)!!.getString(k).toString())
                }
                data.add(dataMap)
//                println()
            } else if (serialNumber == "0"){
                for (k in result.getDictionary(0)!!.keys) {
                    dataMap[k] = SimpleStringProperty(result.getDictionary(0)!!.getString(k).toString())
                }
                data.add(dataMap)
            }
        }
        println("data size = ${data.size}")
        return data
    }
    fun getRecordsForYearAndSerialNumber(year: String, serialNumber: String): ObservableList<Map<String, StringProperty>> {
        println("results for $year")
        val data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
        val recordsWithYear = getRecordsForSerialNumber(serialNumber)
        for (record in recordsWithYear){
            if (record["Date"].toString().contains(year)) {
//                println(record)
                data.add(record)
            }
        }
        return data
    }
//    fun getRecordsForYearAndSerialNumber(year: String, serialNumber: String): ObservableList<Map<String, StringProperty>> {
//        println("results for $year")
//        val data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
//        val listQuery = QueryBuilder.select(SelectResult.all())
//            .from(DataSource.database(database))
////        .where(Expression.property("date").regex(Expression.string("\\b$year\\b")))
//        for (result in listQuery.execute().allResults()) {
//            val dataMap = mutableMapOf<String, StringProperty>()
//            if (year!="0" && result.getDictionary(0)?.getString("Date")!!.contains(year)) {
//                for (k in result.getDictionary(0)!!.keys) {
////                    print(k + " : " + result.getDictionary(0)!!.getString(k) + " ")
//                    dataMap[k] = SimpleStringProperty(result.getDictionary(0)!!.getString(k).toString())
//                }
//                data.add(dataMap)
////                println()
//            } else if (year == "0"){
//                for (k in result.getDictionary(0)!!.keys) {
//                    dataMap[k] = SimpleStringProperty(result.getDictionary(0)!!.getString(k).toString())
//                }
//                data.add(dataMap)
//            }
//        }
//        return data
//    }

    fun getRecordsForMonthYearAndDay(serialNumber: String, year: String, month: String, day: String): ObservableList<Map<String, StringProperty>> {
        val data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
        val recordsWithYearAndMonth = getRecordsForMonthAndYear(serialNumber, year, month)
        for (record in recordsWithYearAndMonth){
            val temp = record["Date"].toString().split(".")[0].split(" ")
//            daysSet.add(temp[temp.size-1])
            if (temp[temp.size-1] == day) {
//                println(record)
                data.add(record)
            }
        }
        return data
    }

//    fun getRecordsForSerialNumbers(checkedSerials: ObservableList<String>?): ObservableList<Map<String, StringProperty>>? {
//        var data : ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
//        if (checkedSerials!!.size == 1)
//             data = getRecordsForSerialNumber(checkedSerials!![0])
//        else {
//        }
//        return data
//    }
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
    println("db count = ${database.count}")

}