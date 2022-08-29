// login
document.getElementById("login-form").addEventListener("submit", function (e) {
	e.preventDefault();
	setLoginMessage("");
	var form = this;
	var urlEncodedDataPairs = [];
	urlEncodedDataPairs.push(`username=${encodeURIComponent(form['username'].value)}`);
	urlEncodedDataPairs.push(`password=${encodeURIComponent(form['password'].value)}`);
	var urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
	
	var xhr = new XMLHttpRequest();
	xhr.addEventListener("readystatechange", function() {
	  if(this.readyState === 4) {
		if (this.status == 200) {
			sessionStorage.setItem('user', xhr.responseText);
			window.location.href = "home.html";
			form.reset();
		}
		else {
			setLoginMessage(this.responseText);
		}
	  }
	});
	
	xhr.open("POST", "Login");
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send(urlEncodedData);
});

// registration
document.getElementById("register-form").addEventListener("submit", function (e) {
	e.preventDefault();
	setRegisterMessage("");
	var form = this;
	var pass1 = form["password1"].value;
	var pass2 = form["password2"].value;
	if (pass1 != pass2) {
		setRegisterMessage("Le password non coincidono");
		return;
	}
	var regex = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i;
	if (!regex.test(form["email"].value)) {
		setRegisterMessage("Indirizzo email non valido");
	}
	var urlEncodedDataPairs = [];
	urlEncodedDataPairs.push(`username=${encodeURIComponent(form['username'].value)}`);
	urlEncodedDataPairs.push(`firstname=${encodeURIComponent(form['firstname'].value)}`);
	urlEncodedDataPairs.push(`lastname=${encodeURIComponent(form['lastname'].value)}`);
	urlEncodedDataPairs.push(`email=${encodeURIComponent(form['email'].value)}`);
	urlEncodedDataPairs.push(`password1=${encodeURIComponent(form['password1'].value)}`);
	urlEncodedDataPairs.push(`password2=${encodeURIComponent(form['password2'].value)}`);
	var urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
	
	var xhr = new XMLHttpRequest();
	xhr.addEventListener("readystatechange", function() {
	  if(this.readyState === 4) {
		if (this.status == 200) {
			setRegisterMessage("Registrazione avvenuta con successo");
			form.reset();
		}
		else {
			setRegisterMessage(this.responseText);
		}
	  }
	});
	
	xhr.open("POST", "Register");
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send(urlEncodedData);
});

var setLoginMessage = (msg) => {
	document.getElementById("error-message1").innerHTML = msg;
}

var setRegisterMessage = (msg) => {
	document.getElementById("error-message2").innerHTML = msg;
}