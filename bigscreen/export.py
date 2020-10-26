from data import SourceData

from flask import Flask, render_template

import time

app = Flask(__name__)

data = SourceData()
data.dump_json()
print("The data has been loaded at: " + time.asctime(time.localtime(time.time())))

if __name__ == "__main__":
    with app.app_context():
        rendered = render_template('index.html', form=data, title=data.title)
    with open('./static/hackathon.html', 'w', encoding='utf-8') as f:
        f.write(rendered)
