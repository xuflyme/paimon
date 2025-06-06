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

package org.apache.paimon.io;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Newly created data files and changelog files. */
public class DataIncrement {

    private final List<DataFileMeta> newFiles;
    private final List<DataFileMeta> deletedFiles;
    private final List<DataFileMeta> changelogFiles;

    public DataIncrement(
            List<DataFileMeta> newFiles,
            List<DataFileMeta> deletedFiles,
            List<DataFileMeta> changelogFiles) {
        this.newFiles = newFiles;
        this.deletedFiles = deletedFiles;
        this.changelogFiles = changelogFiles;
    }

    public static DataIncrement emptyIncrement() {
        return new DataIncrement(
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public List<DataFileMeta> newFiles() {
        return newFiles;
    }

    public List<DataFileMeta> deletedFiles() {
        return deletedFiles;
    }

    public List<DataFileMeta> changelogFiles() {
        return changelogFiles;
    }

    public boolean isEmpty() {
        return newFiles.isEmpty() && changelogFiles.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataIncrement that = (DataIncrement) o;
        return Objects.equals(newFiles, that.newFiles)
                && Objects.equals(changelogFiles, that.changelogFiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newFiles, changelogFiles);
    }

    @Override
    public String toString() {
        return String.format(
                "DataIncrement {newFiles = %s, deletedFiles = %s, changelogFiles = %s}",
                newFiles.stream().map(DataFileMeta::fileName).collect(Collectors.toList()),
                deletedFiles.stream().map(DataFileMeta::fileName).collect(Collectors.toList()),
                changelogFiles.stream().map(DataFileMeta::fileName).collect(Collectors.toList()));
    }
}
