package oriented.free

import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.impls.orient.OrientElement
import oriented.OrientFormat

/**
  * Created by markjong on 05/12/2016.
  */
package object interpreters {

  implicit class RichOrientElement(val orientElement: OrientElement) {

    def writeJson[A](value: A, orientFormat: OrientFormat[A]) = {
      orientElement.getRecord.fromJSON(orientFormat.encode(value)).save()
    }


    def readJson[A](orientFormat: OrientFormat[A]): A = {
      orientFormat.decode(orientElement.getRecord.toJSON).get
    }


  }

}
