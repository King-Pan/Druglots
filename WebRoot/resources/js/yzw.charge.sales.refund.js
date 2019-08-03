function allotEvent(){
		jQuery("#setshoptable").empty().append(`
				<tr>
		      	<td width="98" align="right">&nbsp;收款账户：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="accountCredited" id="accountCredited" />
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;收款账户开户银行：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="openingBank" id="openingBank"/>
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;打款银行：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="moneyBank" id="moneyBank" />
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;金额：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="money" id="money" />
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;交易时间：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="date" name="tradingTime" id="tradingTime" />
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;汇款单号：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="remittanceOrderNo" id="remittanceOrderNo" />
		      	</td>
		      </tr>
		      <tr>
		      	<td width="98" align="right">&nbsp;对应订单号：</td>
		      	<td width="607" style="padding-left:30px;">
		      		<input type="text" name="correspondingOrderNo" id="correspondingOrderNo" />
		      	</td>
		      </tr>
      <tr>
      	<td width="98" align="right">&nbsp;发票：</td>
      	<td width="607" style="padding-left:30px;">
      		<input type="text" name="invoice" id="invoice" />
      	</td>
      </tr>
      <tr>
      	<td width="98" align="right">&nbsp;凭证：</td>
      	<td width="607" style="padding-left:30px;">
      		<input type="text" name="voucher" id="voucher" />
      	</td>
      </tr>
      <tr>
      <td width="98" align="right">&nbsp;</td>
      <td style="padding-left:30px;"><span class="setsub">
        <input name="" type="submit"  value="提交" style="cursor:pointer;"/>
        </span></td>
    </tr>
			`);
	}
	function transferEvent() {
		jQuery("#setshoptable").empty().append(`
		<tr>
	      	<td width="98" align="right">&nbsp;收款账户：</td>
	      	<td width="607" style="padding-left:30px;">
	      		<input type="text" name="accountCredited" id="accountCredited" />
	      	</td>
      	</tr>
	      <tr>
	    	<td width="98" align="right">&nbsp;金额：</td>
	    	<td width="607" style="padding-left:30px;">
	    		<input type="text" name="money" id="money" />
	    	</td>
	    </tr>
	    <tr>
	    	<td width="98" align="right">&nbsp;交易时间：</td>
	    	<td width="607" style="padding-left:30px;">
	    		<input type="date" name="tradingTime" id="tradingTime" />
	    	</td>
	    </tr>
	    <tr>
	    	<td width="98" align="right">&nbsp;汇款单号：</td>
	    	<td width="607" style="padding-left:30px;">
	    		<input type="text" name="remittanceOrderNo" id="remittanceOrderNo" />
	    	</td>
	    </tr>
	    <tr>
	    	<td width="98" align="right">&nbsp;对应订单号：</td>
	    	<td width="607" style="padding-left:30px;">
	    		<input type="text" name="correspondingOrderNo" id="correspondingOrderNo" />
	    	</td>
	    </tr>
		<tr>
		<td width="98" align="right">&nbsp;发票：</td>
		<td width="607" style="padding-left:30px;">
			<input type="text" name="invoice" id="invoice" />
		</td>
		</tr>
		<tr>
		<td width="98" align="right">&nbsp;凭证：</td>
		<td width="607" style="padding-left:30px;">
			<input type="text" name="voucher" id="voucher" />
		</td>
		</tr>
	    <tr>
		    <td width="98" align="right">&nbsp;</td>
		    <td style="padding-left:30px;"><span class="setsub">
		      <input name="" type="submit"  value="提交" style="cursor:pointer;"/>
		      </span></td>
	 	</tr>
		`);
	}