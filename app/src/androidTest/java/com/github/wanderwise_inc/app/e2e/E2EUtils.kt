package com.github.wanderwise_inc.app.e2e

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

object E2EUtils {

  /**
   * This function checks if a given subTestTag is present in the TestTag of a node.
   *
   * @param subTestTag The subTestTag to check for in the TestTag of a node.
   * @return SemanticsMatcher A SemanticsMatcher that can be used to find nodes that contain the
   *   given subTestTag in their TestTag.
   */
  fun hasSubTestTag(subTestTag: String): SemanticsMatcher {
    return SemanticsMatcher("Node with subtag $subTestTag") { node ->
      node.config.getOrNull(SemanticsProperties.TestTag)?.contains(subTestTag) == true
    }
  }
}
