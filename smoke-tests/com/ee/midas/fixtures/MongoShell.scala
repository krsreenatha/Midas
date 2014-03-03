package com.ee.midas.fixtures

import com.mongodb._
import org.specs2.form.Form
import org.specs2.specification.Forms._
import org.bson.BSONObject
import java.util.ArrayList

case class MongoShell(formName: String, host: String, port: Int) {
  val mongoClient = new MongoClient(host, port)
  var db: DB = null
  var shell = Form(formName)
  var documents: ArrayList[DBObject] = null

  def close() = mongoClient.close()

  def useDatabase(dbName: String) = {
    db = mongoClient.getDB(dbName)
    shell = shell.tr(field(s">use $dbName"))
    this
  }

  def runCommand(query: String) = {
    val result = db.doEval(query)
    shell = shell.tr(prop(s">$query", result.ok(), true))
    this
  }

  def readDocuments(collection: String) = {
    documents = new ArrayList[DBObject]()
    val cursor = db.getCollection(collection).find()
    while(cursor.hasNext) {
      val document = cursor.next()
      documents.add(document)
    }
    this
  }

  def update(collection: String, findQuery: DBObject, updateQuery: DBObject) = {
     val result: WriteResult = db.getCollection(collection).update(findQuery, updateQuery)
     val query = collection + ".update(" + findQuery + ", " + updateQuery + ")"
     shell = shell.tr(prop(query, result.get.getField("updatedExisting"), true))
     this
  }
  def verifyIfCopied(newOldFields: Array[(String, String)]) = {
    documents.toArray.foreach({ document =>
      shell = shell.tr(field(s"document", document))
      for(newOldField <- newOldFields)
         verifyIfFieldCopiedIn(document.asInstanceOf[DBObject], newOldField)
      verifyExpansionVersion(document.asInstanceOf[DBObject], newOldFields.length)
    })
    this
  }

  private def verifyIfFieldCopiedIn(document: DBObject,newOldField: (String, String)) = {
     val newField = newOldField._1
     val oldField = newOldField._2
     val newFieldValue = if(newField.contains("."))
       readNestedValue(newField, document)
     else
       document.asInstanceOf[DBObject].get(newField)

     val oldFieldValue = if(oldField.contains("."))
       readNestedValue(oldField, document)
     else
       document.asInstanceOf[DBObject].get(oldField)

     shell = shell.tr(prop(s"document.get(${newField})", newFieldValue, oldFieldValue))
  }

  private def verifyExpansionVersion(document:DBObject, noOfExpansions: Int) = {
      val expansionVersion = document.asInstanceOf[DBObject].get("_expansionVersion")
      shell = shell.tr(prop("document.get('_expansionVersion')", expansionVersion, noOfExpansions))
  }

  def verifyIfRemoved(fields: Array[String]) = {
    documents.toArray.foreach({ document =>
      shell = shell.tr(field(s"document", document))
      for(field <- fields)
         shell = shell.tr(prop(s"!document.containsField($field)", !document.asInstanceOf[DBObject].containsField(field), true))
      shell = shell.tr(prop(s"document.get('_contractionVersion')", document.asInstanceOf[DBObject].get("_contractionVersion"), fields.length))
    })
    this
  }

  private def readNestedValue(fieldName: String, document: Object): Object = {
    val nestedFields = fieldName.split("\\.")
    var fieldValue = document
    for(field <- nestedFields) {
      fieldValue = fieldValue.asInstanceOf[BSONObject].get(field)
    }
    fieldValue
  }

  def retrieve() = {
    close()
    shell
  }
}
