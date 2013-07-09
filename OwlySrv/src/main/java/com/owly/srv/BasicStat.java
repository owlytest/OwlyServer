/*
* Copyright 2013- Antonio Menendez Lopez (tonimenen)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.owly.srv;

import java.util.Date;


/**
 * Class for BasicStat object which contains basic values for the metric ( time of the metric and value of the metric ). 
 *  
 * @author Antonio Menendez Lopez (tonimenen)
 * 
 */

public class BasicStat {
	
	private Date date;
	private String Value;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	@Override
	public String toString() {
		return "BasicStat [date=" + date + ", Value=" + Value + "]";
	}
	
	

}
