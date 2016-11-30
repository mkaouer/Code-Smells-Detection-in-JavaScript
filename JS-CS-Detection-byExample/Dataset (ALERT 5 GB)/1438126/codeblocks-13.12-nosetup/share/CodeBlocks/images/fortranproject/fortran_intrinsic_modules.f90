!**************************************************************************************
!
! This file is part of FortranProject plugin for Code::Blocks IDE.
! It contains list of keywords and intrinsic procedures which are included in code-completion list.
!
! Description of procedures is based on GNU Fortran user manual.
!
! The file is licensed under the GNU General Public License, version 3
! http://www.gnu.org/licenses/gpl-3.0.html
!
! Author: Darius Markauskas
!
!**************************************************************************************

module iso_c_binding
    type c_ptr
    end type

    type c_funptr
    end type

    type(c_ptr), parameter :: c_null_ptr
    type(c_funptr), parameter :: c_null_funptr

    integer, parameter :: c_int
    integer, parameter :: c_short, c_long, c_long_long, c_signed_char, c_size_t, c_int8_t, c_int16_t, c_int32_t, c_int64_t, &
                          c_int_least8_t, c_int_least16_t, c_int_least32_t, c_int_least64_t, c_int_fast8_t, &
                          c_int_fast16_t, c_int_fast32_t, c_int_fast64_t, c_intmax_t, c_intptr_t
    integer, parameter :: c_float, c_double, c_long_double
    integer, parameter :: c_float_complex, c_double_complex, c_long_double_complex
    integer, parameter :: c_bool
    integer, parameter :: c_char
    character(kind=c_char, len=1), parameter :: c_null_char, c_alert, c_backspace, c_form_feed, c_new_line, &
                                                c_carriage_return, c_horizontal_tab, c_vertical_tab

    logical function c_associated(c_ptr_1 [, c_ptr_2])
        ! Determines the status of the C pointer c_ptr_1 or if c_ptr_1 is associated with the target c_ptr_2.
        ! Return value:
        ! The return value is of type LOGICAL; it is .false. if either c_ptr_1 is a C NULL pointer
        ! or if c_ptr1 and c_ptr_2 point to different addresses.
        type(c_ptr or c_funptr) :: c_ptr_1, c_ptr_2
    end function

    subroutine c_f_pointer(cptr, fptr[, shape])
        ! Assign the target the C pointer CPTR to the Fortran pointer FPTR and specify its shape.
        type(c_ptr), intent(in) :: cptr
        type(*), intent(out), pointer :: fptr
        integer, optional, intent(in) :: shape(:)
    end subroutine

    subroutine c_f_procpointer(cptr, fptr)
        ! Assign the target of the C function pointer CPTR to the Fortran procedure pointer FPTR.
        type(c_funptr), intent(in) :: cptr
        procedure(function_interface), pointer :: fptr
    end subroutine

    type(c_funptr) function c_funloc(x)
        ! Determines the C address of the argument.
        ! Return value:
        ! The return value is of type C_FUNPTR and contains the C address of the argument.
        procedure(function_interface) :: x ! Interoperable function or pointer to such function.
    end function

    type(c_ptr) function c_loc(x)
        ! Determines the C address of the argument.
        ! Return value:
        ! The return value is of type C_PTR and contains the C address of the argument.
        type(*), pointer or target :: x
    end function

    integer(c_size_t) function c_sizeof(x)
        ! Calculates the number of bytes of storage the expression X occupies.
        ! Return value:
        ! The return value is of type integer and of the system-dependent kind C_SIZE_T (from the ISO_C_BINDING module).
        ! Its value is the number of bytes occupied by the argument. If the argument has the POINTER attribute,
        ! the number of bytes of the storage area pointed to is returned. If the argument is of a derived type
        ! with POINTER or ALLOCATABLE components, the return value doesn't account for the sizes of the data pointed
        ! to by these components.
        type(*) :: x
    end function

end module iso_c_binding


module iso_fortran_env
    integer, parameter :: atomic_int_kind
    integer, parameter :: atomic_logical_kind
    integer, parameter :: character_kinds(:)
    integer, parameter :: character_storage_size
    integer, parameter :: error_unit
    integer, parameter :: file_storage_size
    integer, parameter :: input_unit
    integer, parameter :: int8, int16, int32, int64
    integer, parameter :: integer_kinds(:)
    integer, parameter :: iostat_end
    integer, parameter :: iostat_eor
    integer, parameter :: iostat_inquire_internal_unit
    integer, parameter :: logical_kinds(:)
    integer, parameter :: numeric_storage_size
    integer, parameter :: output_unit
    integer, parameter :: real_kinds(:)
    integer, parameter :: real32, real64, real128
    integer, parameter :: stat_locked
    integer, parameter :: stat_locked_other_image
    integer, parameter :: stat_stopped_image
    integer, parameter :: stat_unlocked

    type lock_type
    end type

    character(len=*) function compiler_options()
        ! Processor-dependent string describing the options that controlled the program translation phase.
    end function

    character(len=*) function compiler_version()
        ! Processor-dependent string identifying the program translation phase.
    end function

end module iso_fortran_env


module ieee_exceptions
    type ieee_flag_type
    end type

    type ieee_status_type
    end type

    type(ieee_flag_type), parameter :: ieee_invalid
    type(ieee_flag_type), parameter :: ieee_overflow
    type(ieee_flag_type), parameter :: ieee_devide_by_zero
    type(ieee_flag_type), parameter :: ieee_underflow
    type(ieee_flag_type), parameter :: ieee_inexact
    type(ieee_flag_type), parameter :: ieee_usual(3) = [ieee_overflow, ieee_devide_by_zero, ieee_invalid]
    type(ieee_flag_type), parameter :: ieee_all(5) = [ieee_usual, ieee_underflow, ieee_inexact]

    subroutine ieee_get_flag(flag, flag_value)
    end subroutine

    subroutine ieee_get_halting_mode(flag, halting)
    end subroutine

    subroutine ieee_get_status(status_value)
    end subroutine

    subroutine ieee_set_flag(flag, flag_value)
    end subroutine

    subroutine ieee_set_halting_mode(flag, halting)
    end subroutine

    subroutine ieee_set_status(status_value)
    end subroutine

    function ieee_support_flag(flag [,x])
    end function

    function ieee_support_halting(flag)
    end function

end module ieee_exceptions


module ieee_arithmetic
    use ieee_exceptions

    type ieee_class_type
    end type

    type ieee_round_type
    end type

    type(ieee_class_type), parameter :: ieee_signaling_nan
    type(ieee_class_type), parameter :: ieee_quiet_nan
    type(ieee_class_type), parameter :: ieee_negative_inf
    type(ieee_class_type), parameter :: ieee_negative_normal
    type(ieee_class_type), parameter :: ieee_negative_denormal
    type(ieee_class_type), parameter :: ieee_negative_zero
    type(ieee_class_type), parameter :: ieee_positive_zero
    type(ieee_class_type), parameter :: ieee_positive_denormal
    type(ieee_class_type), parameter :: ieee_positive_normal
    type(ieee_class_type), parameter :: ieee_positive_inf
    type(ieee_class_type), parameter :: ieee_other_value

    type(ieee_round_type), parameter :: ieee_nearest
    type(ieee_round_type), parameter :: ieee_to_zero
    type(ieee_round_type), parameter :: ieee_up
    type(ieee_round_type), parameter :: ieee_down
    type(ieee_round_type), parameter :: ieee_other

    type(ieee_class_type) function ieee_class(x)
        ! Classify number
        real :: x
    end function

    function ieee_copy_sign(x, y)
        ! Copy sign.
    end function

    subroutine ieee_get_rounding_mode(round_value)
        ! Get rounding mode.
    end subroutine

    subroutine ieee_get_underflow_mode(gradual)
        ! Get underflow mode.
    end subroutine

    logical function ieee_is_finite(x)
        ! Whether a value is ﬁnite.
        real :: x
    end function

    function ieee_is_nan(x)
        ! Whether a value is an IEEE NaN.
    end function

    function ieee_is_negative(x)
        ! Whether a value is negative.
    end function

    function ieee_is_normal(x)
        ! Whether a value is a normal number.
    end function

    function ieee_logb(x)
        ! Exponent.
    end function

    function ieee_next_after(x, y)
        ! Adjacent machine number.
    end function

    function ieee_rem(x, y)
        ! Exact remainder.
    end function

    function ieee_rint(x)
        ! Round to integer.
    end function

    function ieee_scalb(x, i)
        ! x * 2**i.
    end function

    function ieee_selected_real_kind([p, r, radix])
        ! IEEE kind type parameter value.
    end function

    subroutine ieee_set_rounding_mode(round_value)
        ! Set rounding mode.
    end subroutine

    subroutine ieee_set_underflow_mode(gradual)
        ! Set underflow mode.
    end subroutine

    function ieee_support_datatype([x])
        ! Query IEEE arithmetic support.
    end function

    function ieee_support_denormal([x])
        ! Query denormalized number support.
    end function

    function ieee_support_divide([x])
        ! Query IEEE division support.
    end function

    function ieee_support_inf([x])
    end function

    function ieee_support_io([x])
    end function

    function ieee_support_nan([x])
    end function

    function ieee_support_rounding(round_value [, x])
    end function

    function ieee_support_sqrt([x])
    end function

    function ieee_support_standard([x])
    end function

    function ieee_support_underflow_control([x])
    end function

    function ieee_unordered(x, y)
    end function

    function ieee_value(x, class)
    end function

end module ieee_arithmetic

module ieee_features
    type ieee_features_type
    end type

    type(ieee_features_type), parameter :: ieee_datatype
    type(ieee_features_type), parameter :: ieee_denormal
    type(ieee_features_type), parameter :: ieee_divide
    type(ieee_features_type), parameter :: ieee_halting
    type(ieee_features_type), parameter :: ieee_inexact_flag
    type(ieee_features_type), parameter :: ieee_inf
    type(ieee_features_type), parameter :: ieee_invalid_flag
    type(ieee_features_type), parameter :: ieee_nan
    type(ieee_features_type), parameter :: ieee_rounding
    type(ieee_features_type), parameter :: ieee_sqrt
    type(ieee_features_type), parameter :: ieee_underflow_flag
end module

