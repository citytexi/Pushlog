package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

private val BIRTH_DATE =
    Regex("""\d{2}(?<year>\d{2})(?:(?<sep>[-./])(?<month>\d{2})\k<sep>|(?<compactMonth>\d{2}))(?<day>\d{2})""")
private val MONTHS = 1..12
private val DAYS = 1..31

internal fun digitsOnly(text: CharSequence): String = text.filter { it in '0'..'9' }.toString()

/**
 * yyyy-MM-dd, yyyy.MM.dd, yyyy/MM/dd, yyyyMMdd(앞뒤 공백 허용)를 yyMMdd로. 아니면 null.
 * 월 1..12, 일 1..31만 확인한다.
 */
internal fun normalizeBirthDate(text: CharSequence): String? {
    val match = BIRTH_DATE.matchEntire(text.trim()) ?: return null
    val year = match.groups["year"]?.value.orEmpty()
    val month = (match.groups["month"] ?: match.groups["compactMonth"])?.value.orEmpty()
    val day = match.groups["day"]?.value.orEmpty()
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
