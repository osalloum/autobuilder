package io.github.mattshoe.shoebox.autobuilder.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoBuilder


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultInt(val value: Int)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultLong(val value: Long)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultFloat(val value: Float)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultDouble(val value: Double)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultBoolean(val value: Boolean)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultChar(val value: Char)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultByte(val value: Byte)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultShort(val value: Short)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultString(val value: String)


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Default(val args: Array<String>, val imports: Array<String> = [])
