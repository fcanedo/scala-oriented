package dsl

import java.util.Date

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import oriented.{InMemoryClient, OrientFormat}
import oriented.syntax._

import io.circe.syntax._
import io.circe.generic.auto._

/**
  * Test spec for Read DSL
  */
class ReadSpec extends FlatSpec with Matchers with BeforeAndAfter {

  case class BigDecTest(bigDecimal: BigDecimal)

  implicit val orientClient = InMemoryClient("test")

  "Read big decimal" should "be able to read an decimal from an OrientElement" in {
    val model = BigDecTest(BigDecimal(1000000))
    val bd = orientClient.addVertex(model)
    bd.runGraphUnsafe(enableTransactions = false).element should === (model)
  }

  case class Meta(user: String)

  case class Blog(tid: Long, content: String, meta: Meta)

  "Read Embdedded" should "be able to read an embedded object" in {
    val blogVertex = orientClient
      .addVertex(Blog(1, "Some content", Meta("Thomas")))
      .runGraphUnsafe(enableTransactions = false)

    val blogVertexFromQuery = sql"SELECT FROM Blog WHERE tid = '1'"
      .vertex[Blog]
      .unique
      .runGraphUnsafe(enableTransactions = false)

    blogVertex.element should equal(blogVertexFromQuery.element)
  }

  case class BlogEmbed(tid: Long, blog: Blog, metas: List[Meta])

  "Read embedded" should "be able to read an embedded object in an embedded object" in {
    val metaList = List(Meta("Foo"), Meta("Bar"), Meta("Baz"))
    val embededBlogVertex = orientClient
      .addVertex(BlogEmbed(1, Blog(2, "Bla bla", Meta("Thomasso")), metaList))
      .runGraphUnsafe(enableTransactions = false)

    val embeddedBlogQuery = sql"SELECT FROM BlogEmbed WHERE tid = '1'"
      .vertex[BlogEmbed]
      .unique
      .runGraphUnsafe(enableTransactions = false)

    embededBlogVertex.element should equal(embeddedBlogQuery.element)
    embededBlogVertex.element.metas.size should equal(embeddedBlogQuery.element.metas.size)
  }

  "as" should "work" in {
    val metaList = List(Meta("Foo"), Meta("Bar"), Meta("Baz"))
    val embededBlogVertex = orientClient
      .addVertex(BlogEmbed(1, Blog(2, "Bla bla", Meta("Thomasso")), metaList))
      .runGraphUnsafe(enableTransactions = false)

    val count = sql"SELECT COUNT(*) as count FROM BlogEmbed"
      .as[Long]("count")
      .runGraphUnsafe(enableTransactions = false)

    count shouldNot be(0l)
  }

}
