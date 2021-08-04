/**
 * Utility for client side storage
 */

class localStorageService {
  ls = window.localStorage

  /**
   * Set item in storage
   * @param key - Key to search for item
   * @param value - Value to be saved
   * @returns {boolean} - Success indication
   *
   * @property JSON
   */

  setItem(key, value) {
    value = JSON.stringify(value)
    this.ls.setItem(key, value)
    return true
  }

  /**
   * Search form item in local storage
   * @param key - Key to search for
   * @returns {null|[]} - Value stored or null
   *
   * @property JSON
   */

  getItem(key) {
    let value = this.ls.getItem(key)
    try {
      return JSON.parse(value)
    } catch (e) {
      return null
    }
  }

}

export default new localStorageService();