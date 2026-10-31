const analyzeButton = document.getElementById("analyzeButton");
const usernameInput = document.getElementById("username");

analyzeButton.addEventListener("click", async function() {
    const username = usernameInput.value.trim();

    if (username === "") {
        alert("Please enter an osu! username.");
        return;
    }

    analyzeButton.disabled = true;
    analyzeButton.textContent = "Analyzing...";

    try {
        const response = await fetch(
            `http://localhost:8080/analyze?username=${encodeURIComponent(username)}`
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || "Analysis failed.");
        }

        document.getElementById("playerUsername").textContent = data.username;
        document.getElementById("playerPp").textContent = Math.round(data.pp).toLocaleString();
        document.getElementById("playerRank").textContent =
            data.globalRank == null ? "Unranked" : "#" + data.globalRank.toLocaleString();
        document.getElementById("playerAccuracy").textContent =
            data.accuracy.toFixed(2) + "%";

    } catch (error) {
        alert(error.message);
    } finally {
        analyzeButton.disabled = false;
        analyzeButton.textContent = "Analyze";
    }
});