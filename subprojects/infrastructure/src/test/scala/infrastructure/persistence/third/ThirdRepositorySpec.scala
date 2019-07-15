package infrastructure.test.persistence.third

import domain.model.third.Third
import infrastructure.test.persistence.Exec
import infrastructure.persistence.third.{ThirdPersistentModel, ThirdRepository, ThirdSchema}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import domain.model.third.Third
import test.domain.model.third.{BuildThird, BuildThirdId, BuildThirdProfile}

class ThirdRepositorySpec extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll with Exec {
  val thirdSchema = TableQuery[ThirdSchema]

  test("I can insert a new third") {
    ThirdRepository.save(
      BuildThird.any()
    )

    val rows = exec(thirdSchema.result)

    assert(rows.size === 1)
    assert(rows.isInstanceOf[Vector[_]])
    assert(rows.head.isInstanceOf[ThirdPersistentModel])
  }

  test("I understand how to filter") {
    val query = thirdSchema.filter(_.name === "something").result.statements.mkString
    assert(query === "select `id`, `email`, `username`, `name`, `description`, `salt`, `hashpassword`, `registered_datetime`, `email_confirmed` from `third` where `name` = 'something'")
  }

  test("Read return a Some[third] aggregate on reading an existing Third") {
    ThirdRepository.save(
      BuildThird.any(
        withProfile = BuildThirdProfile.any(
          withEmail = "anyemail"
        )
      )
    )

    val third = ThirdRepository.findByEmail(byEmail = "anyemail")

    assert(third.isInstanceOf[Some[Third]])
  }

  test("Read return a Some[third] aggregate on reading a no existing Third") {
    val third = ThirdRepository.findByEmail(byEmail = "non-existing-email")

    assert(third == None)
  }

  test("exist return true on checking an existing third") {
    ThirdRepository.save(
      BuildThird.any(
        withProfile = BuildThirdProfile.any(
          withEmail = "anyemail"
        )
      )
    )

    val exist = ThirdRepository.exist(byEmail = "anyemail")

    assert(exist === true)
  }

  test("exist return false on checking a non existing third") {
    val exist = ThirdRepository.exist(byEmail = "non-existing")

    assert(exist === false)
  }

  override def beforeEach() {
    exec(thirdSchema.schema.dropIfExists)
    exec(thirdSchema.schema.create)
  }

  override def afterAll() = {
    dbConnection.close()
  }
}
