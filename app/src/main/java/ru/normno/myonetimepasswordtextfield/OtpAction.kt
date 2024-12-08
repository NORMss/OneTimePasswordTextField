package ru.normno.myonetimepasswordtextfield

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangedFieldFocus(val index: Int) : OtpAction
    data object OnKeyboardBack : OtpAction
}