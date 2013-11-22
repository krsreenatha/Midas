package com.ee.midas.interceptor

import org.bson.BSONObject
import com.ee.midas.transform.Transformations._
import com.ee.midas.transform.TransformType

class Transformer {

  def canTransformDocuments(implicit fullCollectionName: String): Boolean =
    canBeApplied(fullCollectionName)

  def transform(document: BSONObject)(implicit fullCollectionName: String): BSONObject =
    //TODO: let the implicit come from midas mode
    map(document)(fullCollectionName, TransformType.EXPANSION)
}
