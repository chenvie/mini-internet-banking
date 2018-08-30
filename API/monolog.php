<?php


// Before: composer require monolog/monolog
// composer autoloader
require_once 'vendor/autoload.php';


// Shortcuts for simpler usage
use \Monolog\Logger;
use \Monolog\Formatter\LineFormatter;
use \Monolog\Handler\StreamHandler;
date_default_timezone_set('Asia/Jakarta');
//use \Monolog\Handler\FirePHPHandler;
// Common logger
$log = new Logger('files');
// Line formatter without empty brackets in the end
$formatter = new LineFormatter(null, null, false, true);
// Debug level handler
$debugHandler = new StreamHandler('debug.log', Logger::DEBUG);
$debugHandler->setFormatter($formatter);
// Info level handler
$infoHandler = new StreamHandler('info.log', Logger::INFO);
$infoHandler->setFormatter($formatter);

// Error level handler
$errorHandler = new StreamHandler('error.log', Logger::ERROR);
$errorHandler->setFormatter($formatter);

//$warningHandler = new StreamHandler('warning.log', Logger::WARNING);
//$warningHandler->setFormatter($formatter);



// This will have both DEBUG INFO and ERROR messages
$log->pushHandler($debugHandler);
// This will have both INFO and ERROR messages
$log->pushHandler($infoHandler);
// This will have only ERROR messages
$log->pushHandler($errorHandler);

//$log->pushHandler($warningHandler);

