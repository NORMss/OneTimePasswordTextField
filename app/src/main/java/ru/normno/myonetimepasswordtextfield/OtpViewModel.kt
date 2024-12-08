package ru.normno.myonetimepasswordtextfield

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val VALID_OTP_CODE = "2024"

class OtpViewModel : ViewModel() {
    val state: StateFlow<OtpState>
        field = MutableStateFlow(OtpState())

    fun onAction(action: OtpAction) {
        when (action) {
            is OtpAction.OnChangedFieldFocus -> {
                state.update {
                    it.copy(
                        focusedIndex = action.index,
                    )
                }
            }

            is OtpAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }

            OtpAction.OnKeyboardBack -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                state.update {
                    it.copy(
                        code = it.code.mapIndexed { index, number ->
                            if (index == previousIndex) {
                                null
                            } else {
                                number
                            }
                        },
                        focusedIndex = previousIndex,
                    )
                }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newCode = state.value.code.mapIndexed { currentIndex, currentNumber ->
            if (currentIndex == index) {
                number
            } else {
                currentNumber
            }
        }
        val wasNumberRemoved = number == null
        state.update {
            it.copy(
                code = newCode,
                focusedIndex = if (wasNumberRemoved || it.code.getOrNull(index) != null) {
                    it.focusedIndex
                } else {
                    getNextFocusedTextFieldIndex(
                        currentCode = it.code,
                        currentFocusedIndex = it.focusedIndex,
                    )
                },
                isValid = if (newCode.none { it == null }) {
                    newCode.joinToString("") == VALID_OTP_CODE
                } else null
            )
        }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentCode: List<Int?>,
        currentFocusedIndex: Int?,
    ): Int? {
        if (currentFocusedIndex == null) {
            return null
        }

        if (currentFocusedIndex == 3) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            code = currentCode,
            currentFocusedIndex = currentFocusedIndex,
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        code: List<Int?>,
        currentFocusedIndex: Int,
    ): Int {
        code.forEachIndexed { index, number ->
            if (index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if (number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}