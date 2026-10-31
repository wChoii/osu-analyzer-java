const analyzeButton = document.getElementById("analyzeButton");
const usernameInput = document.getElementById("username");

analyzeButton.addEventListener("click", function() {
    const username = usernameInput.value.trim();

    if (username === "") {
        alert("Please enter an osu! username.");
        return;
    }

    document.getElementById("playerUsername").textContent = username;
    document.getElementById("playerPp").textContent = "12,500";
    document.getElementById("playerRank").textContent = "#25,000";
    document.getElementById("playerAccuracy").textContent = "98.45%";
});