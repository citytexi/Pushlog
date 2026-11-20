package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

private val BIRTH_DATE =
    Regex(
        """\d{2}(?<year>\d{2})(?:\s*(?<sep>[-./])\s*(?<month>\d{1,2})\s*\k<sep>\s*(?<day>\d{1,2})\.?""" +
            """|(?<compactMonth>\d{2})(?<compactDay>\d{2}))""",
    )
private val MONTHS = 1..12
private val DAYS = 1..31

internal fun digitsOnly(text: CharSequence): String = text.filter { it in '0'..'9' }.toString()

/**
 * yyyy-MM-dd, yyyy.MM.dd, yyyy/MM/dd, yyyyMMdd(앞뒤 공백 허용)를 yyMMdd로. 아니면 null.
 * 구분자가 있으면 월·일은 한 자리여도 되고, 구분자 앞뒤 공백과 끝의 점(`1990. 1. 1.`)도 받는다.
 * 월 1..12, 일 1..31만 확인한다.
 */
internal fun normalizeBirthDate(text: CharSequence): String? {
    val match = BIRTH_DATE.matchEntire(text.trim()) ?: return null
    val year = match.groups["year"]?.value.orEmpty()
    val month = (match.groups["month"] ?: match.groups["compactMonth"])?.value.orEmpty().padStart(2, '0')
    val day = (match.groups["day"] ?: match.groups["compactDay"])?.value.orEmpty().padStart(2, '0')
    return (year + month + day).takeIf { month.toInt() in MONTHS && day.toInt() in DAYS }
}

/**
 * [startIndex] 입력 칸부터 [digits]를 차례로 채운 새 값 목록. [startIndex] 앞 칸은 그대로 둔다.
 * 시작 칸 다음의 가린 칸 앞에서 멈춘다. 시작 칸이 가린 칸이면 아무것도 바꾸지 않는다. 남는 숫자는 버린다.
 * [current]·[lengths]·[masked]는 입력 칸 순서다.
 *
 * 채우는 칸은 기존 내용을 버리고 처음부터 다시 채운다(SEED 붙여넣기와 같다). 숫자가 모자라 닿지 않은 칸은 그대로 둔다.
 * [startIndex]가 범위 밖이거나 [digits]가 비면 아무것도 바꾸지 않는다.
 * 세 목록의 크기가 다르면 [IllegalArgumentException]을 던진다. 메시지에는 값을 싣지 않는다.
 */
internal fun distribute(
    digits: String,
    startIndex: Int,
    current: List<String>,
    lengths: List<Int>,
    masked: List<Boolean>,
): List<String> {
    require(current.size == lengths.size && current.size == masked.size) { "칸 목록 크기가 다르다" }
    if (startIndex !in current.indices || masked[startIndex] || digits.isEmpty()) return current
    val result = current.toMutableList()
    var offset = 0
    for (index in startIndex until result.size) {
        if (offset >= digits.length || (index > startIndex && masked[index])) break
        val end = minOf(offset + lengths[index], digits.length)
        result[index] = digits.substring(offset, end)
        offset = end
    }
    return result
}

/**
 * [distribute]가 같은 인자로 마지막으로 채우는 입력 칸. 아무 칸도 채우지 않으면 null.
 * 포커스를 어디로 옮길지 정할 때 쓴다. 숫자 자체는 받지 않고 개수만 받는다.
 */
internal fun lastDistributedIndex(
    digitCount: Int,
    startIndex: Int,
    lengths: List<Int>,
    masked: List<Boolean>,
): Int? {
    require(lengths.size == masked.size) { "칸 목록 크기가 다르다" }
    if (startIndex !in lengths.indices || masked[startIndex] || digitCount <= 0) return null
    var remaining = digitCount
    var last = startIndex
    for (index in startIndex until lengths.size) {
        if (remaining <= 0 || (index > startIndex && masked[index])) break
        last = index
        remaining -= lengths[index]
    }
    return last
}
