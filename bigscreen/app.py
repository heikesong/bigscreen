#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# @Time : 2020/8/26 14:48
# @Author : way
# @Site :
# @Describe:

from flask import Flask, render_template
from data import SourceData

app = Flask(__name__)


@app.route('/')
def index():
    data = SourceData()
    with open('./static/hackathon.html', 'w') as f:
        f.write(render_template('index.html', form=data, title=data.title))
    return render_template('index.html', form=data, title=data.title)


if __name__ == "__main__":
    app.run(host='127.0.0.1', debug=False)
