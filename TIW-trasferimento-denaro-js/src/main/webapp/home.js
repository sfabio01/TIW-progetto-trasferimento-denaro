var user = JSON.parse(sessionStorage.getItem("user"));
var accountList = [];
var selectedAccount = 0;
var accountDetails = {};
var contacts = [];

if (user == null) {
	window.location.href = "index.html";
}

console.log(user); // debug
document.getElementById("welcomeMessage").innerHTML = `Ciao, ${user['firstname']} ${user['lastname']}`

// fetch account list
var xhr = new XMLHttpRequest();
xhr.addEventListener("readystatechange", function() {
  if(this.readyState === 4) {
	if (this.status === 200) {
		accountList = JSON.parse(this.responseText);
	    var accountListElement = document.getElementById("accountList");
	    for(let item of accountList) {
			var li = document.createElement("li");
			var a = document.createElement("a");
			a.href = "#";
			a.innerHTML = "Conto corrente n."+item;
			a.addEventListener("click", (e) => {
				e.preventDefault();
				fetchAccountDetails(item);		
			})
			
			li.appendChild(a);
			accountListElement.appendChild(li);
		}
		// select first account of the list by default
		if (accountList.length > 0) {
			fetchAccountDetails(accountList[0]);
		}
	}
	if (this.status === 401) {
		window.location.href = "index.html";
	}
    if (this.status === 500) {
		setInfoMessage(this.responseText);
	}
  }
});
xhr.open("GET", "GetAccountList");
xhr.send();


// fetch contacts list
var xhr1 = new XMLHttpRequest();
xhr1.addEventListener("readystatechange", function() { 
	if(this.readyState === 4) {
		if (this.status === 200) {
			contacts = JSON.parse(this.responseText);
			updateContactListView();
		} else {
			if (this.status === 401) {
				window.location.href = "index.html";
			} else {
				setInfoMessage(this.responseText);
			}
		}
	}
});

xhr1.open("GET", "Contacts");
xhr1.send();


// create transfer
document.getElementById("createTransferForm").addEventListener("submit", function (e) {
	e.preventDefault();
	document.getElementById("transferMessage").innerHTML = "";
	var form = this;
	var urlEncodedDataPairs = [];
	urlEncodedDataPairs.push(`amount=${encodeURIComponent(form['amount'].value)}`);
	urlEncodedDataPairs.push(`reason=${encodeURIComponent(form['reason'].value)}`);
	urlEncodedDataPairs.push(`username=${encodeURIComponent(form['username'].value)}`);
	urlEncodedDataPairs.push(`accountId=${encodeURIComponent(form['accountId'].value)}`);
	urlEncodedDataPairs.push(`fromAccount=${selectedAccount}`);
	var urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
	
	var xhr = new XMLHttpRequest();
	xhr.addEventListener("readystatechange", function() {
	  if(this.readyState === 4) {
		if (this.status == 200) {
			var summary = JSON.parse(this.responseText);
			console.log(summary);
			var msg = "CONFERMA TRASFERIMENTO\n";
			msg += `Importo: ${summary['transfer']['amount']}\n`
			msg += `Data: ${summary['transfer']['date']}\n`
			msg += `CONTO CORRENTE N.${summary['fromAccountOld']['id']}\n`
			msg += `Saldo precedente: ${summary['fromAccountOld']['balance']}\n`
			msg += `Saldo attuale: ${summary['fromAccountNew']['balance']}\n`
			msg += `CONTO CORRENTE N.${summary['toAccountOld']['id']}\n`
			msg += `Saldo precedente: ${summary['toAccountOld']['balance']}\n`
			msg += `Saldo attuale: ${summary['toAccountNew']['balance']}\n`
			alert(msg);
			saveContact(form['username'].value);
			form.reset();
			accountDetails['transfers'].splice(0,0,summary['transfer']);
			accountDetails['balance'] = summary['fromAccountNew']['balance'];
			console.log(accountDetails);
			updateAccountDetailsView();
		}
		else {
			if (this.status === 403) {
				document.getElementById("transferMessage").innerHTML = "Non sei autorizzato ad effettuare il trasferimento"
			} else{
				document.getElementById("transferMessage").innerHTML = this.responseText;
			}
			
		}
	  }
	});
	
	xhr.open("POST", "CreateTransfer");
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send(urlEncodedData);
});

var fetchAccountDetails = (id) => {
	var xhr = new XMLHttpRequest();
	xhr.addEventListener("readystatechange", function() {
		if (xhr.readyState === 4) {
			if (this.status === 200) {
				accountDetails = JSON.parse(this.responseText);
				selectedAccount = id;
				updateAccountDetailsView();
			} 
			if (this.status === 401) {
				window.location.href = "index.html";
			}
			if (this.status === 403) {
				setInfoMessage("Non puoi visualizzare i dettagli del conto");
			}
			if (this.status === 500) {
				setInfoMessage(this.responseText);
			}
		}
	});
	xhr.open("GET", "GetAccountDetails?id="+id);
	xhr.send();
}


var updateAccountDetailsView = () => {
	document.getElementById("selectedAccount").innerHTML = "Conto corrente n."+accountDetails['id'];
	document.getElementById("accountBalance").innerHTML = "Saldo $"+accountDetails['balance'];
	var tableBody = document.getElementById("tableBody");
	tableBody.innerHTML = "";
	for (let transfer of accountDetails['transfers']) {
		var tr = document.createElement("tr");
		var td1 = document.createElement("td");
		td1.innerHTML = "$"+transfer['amount'];
		tr.appendChild(td1);
		var td2 = document.createElement("td");
		td2.innerText = transfer['reason'];
		tr.appendChild(td2);
		var td3 = document.createElement("td");
		td3.innerHTML = transfer['fromAccount'];
		tr.appendChild(td3);
		var td4 = document.createElement("td");
		td4.innerHTML = transfer['toAccount'];
		tr.appendChild(td4);
		var td5 = document.createElement("td");
		td5.innerHTML = transfer['date'];
		tr.appendChild(td5);
		
		tableBody.appendChild(tr);
	}
};

var saveContact = (username) => {
	if (contacts.includes(username)) return;
	
	var ans = confirm("Vuoi salvare " + username + " in rubrica?");
	if (ans == true) {
		var xhr = new XMLHttpRequest();
		var urlEncodedDataPairs = [];
		urlEncodedDataPairs.push(`userId=${encodeURIComponent(user['id'])}`);
		urlEncodedDataPairs.push(`contactName=${encodeURIComponent(username)}`);
		var urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
		
		xhr.addEventListener("readystatechange", function() { 
			if(this.readyState === 4) {
				if (this.status === 200) {
					contacts.push(username);
					updateContactListView();
				} else {
					if (this.status === 401) {
						window.location.href = "index.html";
					} else {
						setInfoMessage(this.responseText);
					}
				}
			}
		});
		
		xhr.open("POST", "Contacts");
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		xhr.send(urlEncodedData);
	}
}

var updateContactListView = () => {
	var datalist = document.getElementById("contactList");
	datalist.innerHTML = "";
	for (var item of contacts) {
        var option = document.createElement("option");
        option.value = item;
        option.innerHTML = item;
        datalist.appendChild(option);
	}
}

var setInfoMessage = (msg) => {
	document.getElementById("infoMessage").innerHTML = msg;
}
