package infrastructure.test.persistence.user.UserRepository

import infrastructure.persistence.user.{UserPersistentModel, UserRepository, UserSchema}
import infrastructure.test.persistence.Exec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery
import test.domain.model.user.BuildUser

class UserRepositoryOnSaveSpec extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll with Exec {
  val userSchema = TableQuery[UserSchema]

  test("Can insert a new user") {
    UserRepository.save(
      BuildUser.anyNoPersisted()
    )

    val rows = exec(userSchema.result)

    assert(rows.size === 1)
    assert(rows.isInstanceOf[Vector[_]])
    assert(rows.head.isInstanceOf[UserPersistentModel])
  }

  test("Surrogate id is generated once is saved") {
    UserRepository.save(
      BuildUser.anyNoPersisted()
    )

    val rows = exec(userSchema.result)

    assert(rows.size === 1)
    assert(rows.isInstanceOf[Vector[_]])
    assert(rows.head.isInstanceOf[UserPersistentModel])
    println(rows.head)
  }

  override def beforeEach() {
    exec(userSchema.schema.dropIfExists)
    exec(userSchema.schema.create)
  }

  override def afterAll() = {
    dbConnection.close()
  }
}
