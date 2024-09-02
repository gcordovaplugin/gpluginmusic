var exec = require("cordova/exec");

var GMusic = {
  _call: function (callname, arg0, success, error) {
    exec(success, error, "gpluginmusic", callname, [arg0]);
  },
  //初始化
  init: function (success, error) {
    this._call("timer", "", success, error);
  },
  //清空队列
  clean: function () {
    this._call(
      "clean",
      "",
      function () {},
      function () {}
    );
  },
  //返回播放列表
  list: function (success, error) {
    this._call("list", "", success, error);
  },
  //添加列表
  addlist: function (mlist) {
    this._call(
      "addlist",
      mlist,
      function () {},
      function () {}
    );
  },
  //添加一个文件
  addlist: function (item) {
    this._call(
      "add",
      item,
      function () {},
      function () {}
    );
  },
  play: function (index) {
    this._call(
      "play",
      index,
      function () {},
      function () {}
    );
  },
  pause: function () {
    this._call(
      "pause",
      "",
      function () {},
      function () {}
    );
  },
  resume: function () {
    this._call(
      "resume",
      "",
      function () {},
      function () {}
    );
  },
  isPlay: function (success) {
    this._call("isPlay", "", success, function () {});
  },
  hasNext: function (success) {
    this._call("hasNext", "", success, function () {});
  },
  hasPre: function (success) {
    this._call("hasPre", "", success, function () {});
  },
  seek: function (rate) {
    this._call(
      "seek",
      rate,
      function () {},
      function () {}
    );
  },
  next: function () {
    this._call(
      "next",
      "",
      function () {},
      function () {}
    );
  },
  pre: function () {
    this._call(
      "pre",
      "",
      function () {},
      function () {}
    );
  },
};
module.exports = GMusic;
