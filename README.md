# autowire-electron

**Source:** [https://github.com/mscharley/autowire-electron](https://github.com/mscharley/autowire-electron)  
**Author:** Matthew Scharley  
**Contributors:** [See contributors on GitHub][gh-contrib]  
**Bugs/Support:** [Github Issues][gh-issues]  
**Copyright:** 2016  
**License:** [MIT license][license]  
**Status:** Active

## Synopsis

`autowire-electron` is a transport library for autowire allowing usage of Electron's IPC calls
to send messages between the main process and renderer processes.

## Installation

    "com.mscharley" %%% "autowire-electron" % "0.1.0"

## Usage

```scala
import autowire._
import autowire.electronipc._
import electron._
import nodejs.{raw => nodejsraw}
import upickle.{default => upickle}
import scala.scalajs.js.Dynamic.global

// Define your API.
trait Api {
  def ping(): String
}

class ApiImpl extends Api {
  def ping() = {
    global.console.log("ping")
    "pong"
  }
}

// Configure a server class with a serialisation library...
class ApiServer(protected val ipcMain: nodejsraw.EventEmitter)
  extends ElectronIpcWireServer[upickle.Reader, upickle.Writer]
{
  protected val apiImpl = new ApiImpl
  protected val router = this.route[Api](apiImpl)

  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
}

// ... and a client too!
class ApiClient(protected val ipcRenderer: ipc.IpcRenderer)
  extends ElectronIpcWireClient[upickle.Reader, upickle.Writer]
{
  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
}

// Main process.
class App(dirName: String, require: Require) extends ElectronApp(require) with js.JSApp {
  // Keep a global reference of the window object, if you don't, the window will
  // be closed automatically when the JavaScript object is garbage collected.
  var mainWindow: Option[BrowserWindow] = None
  val console = global.console
  val api = new ApiServer(electron.ipcMain)
}

// Renderer process.
class Window(val require: Require = global.require.asInstanceOf[Require])
  extends RendererProcess(require)
{
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
  val jQuery = require("jquery").asInstanceOf[JQueryStatic]

  val api: ApiClient = new ApiClient(electron.ipcRenderer)
  val pong: Future[String] = api[Api].ping().call()

  pong foreach { v =>
    global.console.log(v)
  }

  pong recover {
    case e => global.console.error(s"${e.getClass.getName}: ${e.getMessage}")
  }
}
```

  [gh-contrib]: https://github.com/mscharley/autowire-electron/graphs/contributors
  [gh-issues]: https://github.com/mscharley/autowire-electron/issues
  [license]: https://github.com/mscharley/autowire-electron/blob/master/LICENSE
