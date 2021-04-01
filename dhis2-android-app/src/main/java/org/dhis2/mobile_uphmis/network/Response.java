/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile_uphmis.network;

public class Response {
	public static final String CODE = "networkResponseCode";
	public static final String BODY = "body";
	public static final String EMPTY_RESPONSE = "";

	public static final String CATEGORY_OPTION_COMBOS_KEY = "categoryOptionCombos";
	public static final String CATEGORY_COMBO_KEY = "categoryCombo";
	public static final String CATEGORY_COMBOS_KEY = "categoryCombos";
	public static final String ID_KEY = "id";
	public static final String DATA_ELEMENT_KEY = "dataElement";
	public static final String SECTION_NAME_KEY = "name";
	public static final String DATA_SET_KEY = "dataSetElements";
	public static final String SECTIONS_KEY = "sections";

	private final int code;
	private final String body;

	public Response(int code, String body) {
		this.code = code;
		this.body = body;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getBody() {
		return body;
	}
}