/*******************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 *     Copyright (C) 2014  CafedeRed
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.cafedered.midban.utils.exceptions;

public class SynchronizationErrorException extends Exception {

    private static final long serialVersionUID = 501799127330220072L;
    private String descriptiveErrorMessage;
    
    public SynchronizationErrorException(Throwable e, String descriptiveErrorMessage) {
        super(e);
        this.setDescriptiveErrorMessage(descriptiveErrorMessage);
    }

    public String getDescriptiveErrorMessage() {
        return descriptiveErrorMessage;
    }

    public void setDescriptiveErrorMessage(String descriptiveErrorMessage) {
        this.descriptiveErrorMessage = descriptiveErrorMessage;
    }
}
