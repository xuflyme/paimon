/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.paimon.spark.extensions

import org.apache.spark.sql.SparkSessionExtensions
import org.apache.spark.sql.catalyst.analysis.{PaimonAnalysis, PaimonCoerceArguments, PaimonDeleteTable, PaimonMergeInto, PaimonPostHocResolutionRules, PaimonProcedureResolver, PaimonUpdateTable}
import org.apache.spark.sql.catalyst.parser.extensions.PaimonSparkSqlExtensionsParser
import org.apache.spark.sql.catalyst.plans.logical.PaimonTableValuedFunctions
import org.apache.spark.sql.execution.PaimonStrategy

/** Spark session extension to extends the syntax and adds the rules. */
class PaimonSparkSessionExtensions extends (SparkSessionExtensions => Unit) {

  override def apply(extensions: SparkSessionExtensions): Unit = {
    // parser extensions
    extensions.injectParser { case (_, parser) => new PaimonSparkSqlExtensionsParser(parser) }

    // analyzer extensions
    extensions.injectResolutionRule(sparkSession => new PaimonAnalysis(sparkSession))
    extensions.injectResolutionRule(spark => PaimonProcedureResolver(spark))
    extensions.injectResolutionRule(_ => PaimonCoerceArguments)

    extensions.injectPostHocResolutionRule(spark => PaimonPostHocResolutionRules(spark))

    extensions.injectPostHocResolutionRule(_ => PaimonUpdateTable)
    extensions.injectPostHocResolutionRule(_ => PaimonDeleteTable)
    extensions.injectPostHocResolutionRule(spark => PaimonMergeInto(spark))

    // table function extensions
    PaimonTableValuedFunctions.supportedFnNames.foreach {
      fnName =>
        extensions.injectTableFunction(
          PaimonTableValuedFunctions.getTableValueFunctionInjection(fnName))
    }

    // planner extensions
    extensions.injectPlannerStrategy(spark => PaimonStrategy(spark))
  }
}
