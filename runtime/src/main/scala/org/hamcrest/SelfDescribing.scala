package org.hamcrest

/*
 * Ported from https://github.com/hamcrest/JavaHamcrest/
 */

/**
 * The ability of an object to describe itself.
 */
trait SelfDescribing {
  /**
   * Generates a description of the object.  The description may be part of a
   * a description of a larger object of which this is just a component, so it
   * should be worded appropriately.
   *
   * @param description
   *     The description to be built or appended to.
   */
  def describeTo(description: Description): Unit
}