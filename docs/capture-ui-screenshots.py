from pathlib import Path
import os
from playwright.sync_api import sync_playwright

BASE_URL = os.environ.get("APP_URL", "https://rest-assured-test-automation.onrender.com")
OUT_DIR = Path(__file__).resolve().parent / "screenshots"
OUT_DIR.mkdir(parents=True, exist_ok=True)
VIEWPORT = {"width": 1440, "height": 1200}


def capture(name, action=None):
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page(viewport=VIEWPORT)
        page.goto(f"{BASE_URL}/dashboard.html", wait_until="domcontentloaded")
        page.wait_for_load_state("networkidle")
        if action:
            action(page)
        page.screenshot(path=str(OUT_DIR / name), full_page=True)
        browser.close()
        print(f"Captured {name}")


def auth_options(page):
    page.select_option("#authType", "BEARER_TOKEN")
    page.wait_for_selector("#bearerTokenFields", state="visible")


def expected_source(page):
    page.select_option("#expectedResponseSource", "API_URL")
    page.wait_for_selector("#expectedUrlSection", state="visible")


def results_success(page):
    page.fill("#endpoint", "https://jsonplaceholder.typicode.com/posts/1")
    page.select_option("#method", "GET")
    page.select_option("#authType", "NONE")
    page.click('button:has-text("Generate & Run Tests")')
    page.wait_for_selector("#resultsTable tbody tr", state="visible", timeout=60000)
    page.wait_for_selector("#responseComparison", state="visible", timeout=60000)


capture("01-dashboard-initial.png")
capture("02-auth-options.png", auth_options)
capture("03-expected-response-source.png", expected_source)
capture("04-results-success.png", results_success)
