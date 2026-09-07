import base64, json, os, time
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import URLError, HTTPError
auth = base64.b64encode(("ci-admin:" + os.environ["JENKINS_TEST_PASSWORD"]).encode()).decode()
base = "http://127.0.0.1:8080/job/taski-sprint11/lastBuild"
def get(path):
    with urlopen(Request(base + path, headers={"Authorization": "Basic " + auth}), timeout=15) as response:
        return response.read()
for attempt in range(120):
    try:
        data = json.loads(get("/api/json"))
        if not data["building"]:
            console = get("/consoleText").decode()
            for key in ("DOCKER_USER", "DOCKER_TOKEN", "JENKINS_TEST_PASSWORD"):
                secret = os.environ.get(key)
                if secret:
                    console = console.replace(secret, "***")
            Path("jenkins-console.log").write_text(console)
            Path("jenkins-result.json").write_text(json.dumps({"number": data["number"], "result": data["result"], "duration_ms": data["duration"]}, indent=2))
            print("Jenkins result:", data["result"], "duration_ms:", data["duration"])
            print("\n".join(line for line in console.splitlines() if "PASS " in line or "Finished:" in line or "ERROR" in line))
            if data["result"] != "SUCCESS":
                raise SystemExit(1)
            break
    except (URLError, HTTPError):
        pass
    time.sleep(5)
else:
    raise RuntimeError("Jenkins build did not finish within ten minutes")
