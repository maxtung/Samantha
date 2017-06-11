function answer() {
    console.log(q);
    var q = document.getElementById("q").value;
    if (q == "") {
        return;
    }
    
    location.href = "answer.jsp?q=" + q;
}

function viewAnswer() {
	console.log(q);
    var q = document.getElementById("q").value;
    if (q == "") {
        return;
    }
    
    window.location = "answer.jsp?q=" + q;
}