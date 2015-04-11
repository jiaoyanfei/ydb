var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');


var routes = require('./routes/index');
var login = require('./routes/login');
var logout = require('./routes/logout');

var orderByStyle = require('./routes/orderByStyle');
var orderByMatch = require('./routes/orderByMatch');
var orderByDisplay = require('./routes/orderByDisplay');
var searchByCode = require('./routes/searchByCode');
var detail = require('./routes/detail');
var detailMatch = require('./routes/detailMatch');
var detailDisplay = require('./routes/detailDisplay');
var comment = require('./routes/comment');
var orderBudget = require('./routes/orderBudget');
var collection = require('./routes/collection');
var collectionCancle = require('./routes/collectionCancle');
var order = require('./routes/order');
var alreadyOrdered = require('./routes/alreadyOrdered');
var notOrder = require('./routes/notOrder');
var deleted = require('./routes/deleted');
var invalidPd = require('./routes/invalidPd');
var collectionSet = require('./routes/collectionSet');
var show = require('./routes/show');
var goldAgent = require('./routes/goldAgent');
var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
  name: 'sid',
  secret: 'recommand', // 建议使用 128 个字符的随机字符串
  cookie: { maxAge: null}, //{ maxAge: 10*60 * 1000 },
  resave:false,
  saveUninitialized:true
}));


app.use('/', routes);
app.use('/index', routes);
app.use('/login',login);
app.use('/logout',logout);
app.use('/orderByStyle',orderByStyle);
app.use('/orderByMatch',orderByMatch);
app.use('/orderByDisplay',orderByDisplay);
app.use('/searchByCode',searchByCode);
app.use('/detail', detail);
app.use('/detailMatch',detailMatch);
app.use('/detailDisplay',detailDisplay);
app.use('/comment',comment);
app.use('/orderBudget',orderBudget);
app.use('/collection',collection);
app.use('/collectionCancle',collectionCancle);
app.use('/order',order);
app.use('/alreadyOrdered',alreadyOrdered);
app.use('/notOrder',notOrder);
app.use('/invalidPd',invalidPd);
app.use('/deleted',deleted);
app.use('/collectionSet',collectionSet);
app.use('/show',show);
app.use('/goldAgent',goldAgent);
// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});


module.exports = app;
