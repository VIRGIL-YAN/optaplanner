/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.dinnerparty.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("Seat")
public class Seat extends AbstractPersistable {

    private Table table;
    private int seatIndexInTable;

    private Seat leftSeat;
    private Seat rightSeat;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getSeatIndexInTable() {
        return seatIndexInTable;
    }

    public void setSeatIndexInTable(int seatIndexInTable) {
        this.seatIndexInTable = seatIndexInTable;
    }

    public Seat getLeftSeat() {
        return leftSeat;
    }

    public void setLeftSeat(Seat leftSeat) {
        this.leftSeat = leftSeat;
    }

    public Seat getRightSeat() {
        return rightSeat;
    }

    public void setRightSeat(Seat rightSeat) {
        this.rightSeat = rightSeat;
    }

    public Gender getRequiredGender() {
        return (seatIndexInTable % 2 == 0) ? Gender.MALE : Gender.FEMALE;
    }

    public String getLabel() {
        return "Table " + table.getTableIndex() + " seat " + seatIndexInTable;
    }

    @Override
    public String toString() {
        return table + "." + seatIndexInTable;
    }


}
