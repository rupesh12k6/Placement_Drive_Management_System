import { useState } from 'react'

export function useForm(initialValues = {}, validate) {
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState({})
  const [touched, setTouched] = useState({})

  // 🔹 Handle input change
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target

    const newValue = type === 'checkbox' ? checked : value

    setValues((prev) => {
      const updatedValues = { ...prev, [name]: newValue }

      // run validation on change if validator exists
      if (validate) {
        const validationErrors = validate(updatedValues) || {}
        setErrors((prevErr) => ({
          ...prevErr,
          [name]: validationErrors[name] || ''
        }))
      }

      return updatedValues
    })
  }

  // 🔹 Handle blur (field touched)
  const handleBlur = (e) => {
    const { name } = e.target

    setTouched((prev) => ({ ...prev, [name]: true }))

    if (validate) {
      const validationErrors = validate(values) || {}
      setErrors((prevErr) => ({
        ...prevErr,
        [name]: validationErrors[name] || ''
      }))
    }
  }

  // 🔹 Handle submit
  const handleSubmit = (onSubmit) => async (e) => {
    e.preventDefault()

    let validationErrors = {}

    if (validate) {
      validationErrors = validate(values) || {}
      setErrors(validationErrors)

      // mark all fields as touched
      const allTouched = Object.keys(values).reduce((acc, key) => {
        acc[key] = true
        return acc
      }, {})
      setTouched(allTouched)

      // stop if any error exists
      if (Object.values(validationErrors).some(Boolean)) return
    }

    // support async submit
    await onSubmit(values)
  }

  // 🔹 Reset form
  const reset = () => {
    setValues(initialValues)
    setErrors({})
    setTouched({})
  }

  return {
    values,
    errors,
    touched,
    handleChange,
    handleBlur,
    handleSubmit,
    reset,
    setValues
  }
}