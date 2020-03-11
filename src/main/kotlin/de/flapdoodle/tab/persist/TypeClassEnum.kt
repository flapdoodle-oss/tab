package de.flapdoodle.tab.persist

import kotlin.reflect.KClass

interface TypeClassEnum<E : Enum<E>, T : Any> {
  val type: KClass<out T>

  companion object {
    inline fun <reified ET, T : Any> typeOf(type: KClass<out T>): ET
        where  ET : Enum<ET>, ET : TypeClassEnum<ET, T> {
      return typeOf(ET::class, type)
    }

    fun <ET, T : Any> typeOf(enumType: KClass<ET>, type: KClass<out T>): ET
        where ET : Enum<ET>,
              ET : TypeClassEnum<ET, T> {
      val enumConstants = enumType.java.enumConstants
      enumConstants.forEach {
        if (it.type == type) return it
      }
      throw IllegalArgumentException("could not find a matching entry in $enumConstants for $type")
    }

    fun <T: TypeClassEnum<out Any, out Any>> typeClassOf(type: T): KClass<out Any> {
      return type.type
    }
  }
}