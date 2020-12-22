<div align="center">

# `discore`

![GitHub last commit](https://img.shields.io/github/last-commit/zp4rker/discore?style=flat)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/zp4rker/discore?label=current+version&style=flat)
![GitHub](https://img.shields.io/github/license/zp4rker/discore?style=flat)
![Lines of code](https://img.shields.io/tokei/lines/github/zp4rker/discore?style=flat)
[![Discord Badge](https://discordapp.com/api/guilds/647312158832721934/widget.png)](https://zp4rker.com/discord)

</div>

A Discord bot core built upon JDA, written in Kotlin.

# Current features

## Embed Constructor

A simpler, cleaner way to create a `MessageEmbed`. The embed constructor has two forms: the [inline](#inline-constructor) form, and the [structured](#structured-constructor) form. The embed constructor consists of multiple components, all of which are optional. They are as follows:

| Name          | Type              | Example
| ---           | ---               | ---
| `author`      | EmbedAuthor       | [See below](#embedauthor)
| `color`       | String            | `"#2f3136"`
| `description` | String            | `"Total stats"`
| `dooter`      | EmbedFooter       | [See below](#embedfooter)
| `image`       | String            | `"https://img.com/img.png"`
| `thumbnail`   | String            | `"https://img.com/img2.jpg"`
| `timestamp`   | TemporalAccessor  | `Instant.now()`
| `title`       | EmbedTitle        | [See below](#embedtitle)
| `fields`      | List\<EmbedField> | [See below](#embedfield)

The complex components consist of the following attributes:

#### EmbedAuthor

| Name      | Type      | Example                       | Default
| ---       | ---       | ---                           | ---
| `name`    | String    | `"zp4rker"`                   | `null`
| `url`     | String    | `"https://google.com"`        | `null`
| `iconUrl` | String    | `"https://img.com/avtr.com"`  | `null`

#### EmbedFooter

| Name      | Type      | Example                       | Default
| ---       | ---       | ---                           | ---
| `text`    | String    | `"Created by zp4rker#3333"`   | `null`
| `iconUrl` | String    | `"https://img.com/avtr.com"`  | `null`

#### EmbedTitle

| Name      | Type      | Example                       | Default
| ---       | ---       | ---                           | ---
| `text`    | String    | `"Cool embed title"`          | `"Title"`
| `url`     | String    | `"https://google.com"`        | `null`

#### EmbedField

| Name      | Type      | Example                       | Default
| ---       | ---       | ---                           | ---
| `title`   | String    | `"Current level"`             | `EmbedBuilder.ZERO_WIDTH_SPACE`
| `text`    | String    | `"Lvl. 47"`                   | `EmbedBuilder.ZERO_WIDTH_SPACE`
| `inline`  | Boolean   | `false`                       | `true`

### Inline Constructor

```kotlin
// Example 1
embed(author = author(name = "zp4rker", iconUrl = "https://img.avatar.com/zp4rker.png"), color = "#568abe", description = "This the embed content.")

// Example 2
embed(footer = footer(text = "Made by zp4rker#3333"), fields = listOf(field(title = "embed #1", text = "here is some text"), field(title = "empty text field"), field(title = "non-inline embed", text = "some text", inline = false)))
```

### Structured Constructor

```kotlin
// Example 1
embed {
    author {
        name = "zp4rker"
        iconUrl = "https://img.avatar.com/zp4rker.png"
    }
    
    color = "#568abe"
    
    description = "This is the embed content."
}

// Example 2
embed {
    footer {
        text = "Made by zp4rker#3333"
    }
    
    field {
        title = "embed #1"
        text = "here is some text"
    }
    
    field {
        title = "empty text field"
    }
    
    field {
        title = "non-inline embed"
        text = "some text"
        inline = false
    }
}
```

---

## Bot constructor

A simple, structured and clean way to build the JDA instance. Below are the current options available within the constructor:

| Name                  | Type              | Default                   | Description
| ---                   | ---               | ---                       | ---
| `name`                | String            | `"Discore"`               | The name of the bot, used for the default logger.
| `version`             | String            | `"1.0.0"`                 | The bot's version, used in log outputs.
| `token`               | String            | `"empty"`                 | The Discord bot token, used to authenticate the bot. **Required**
| `prefix`              | String            | `"/"`                     | The prefix for the [command handler](#command-handler).
| `helpCommandEnabled`  | Boolean           | `true`                    | Whether or not the default help command should be registered.
| `commands`            | List\<Command>    | `listOf()`                | The list of commands to register on startup.
| `activity`            | Activity          | `null`                    | The activity/status of the bot.
| `intents`             | Int               | `GatewayIntent.DEFAULT`   | The gateway intents the bot should start with.
| `cache`               | List\<CacheFlag>  | `listOf()`                | Whether or not cache should be enabled.
| `quit`                | () -> Unit        | `{}`                      | The function to run when the bot is quitting.

```kotlin
// Example
bot {
    name = "MySuperCoolBot"
    version = "1.3.7"
    token = "secure.code"
    
    prefix = "!"
    commands = arrayOf(PingCommand, FunCommand, PurgeCommand)
    
    activity = Activity.playing("something awesome")
    
    quit = {
        logger.info("Now quitting...")
        // run some cleanup code
    }
}
```

Invoking the bot constructor returns an instance of the `Bot` class, which has two methods to register commands or event listeners: `addCommands(vararg commands: Command)` & `addEventListeners(vararg listeners: EventListener)`. When using the bot constructor, the [`IEventManager`](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/hooks/IEventManager.html) is set to [`InterfacedEventManager()`](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/hooks/InterfacedEventManager.html). Using the bot constructor also initialises the following constants:

| Name                  | Class
| ---                   | ---
| `API`                 | JDA
| `BOT`                 | Bot
| `MANIFEST`            | Attributes
| `LOGGER`              | Logger _(not to be confused with the Bot logger)_
| `HIDDEN_EMBED_COLOUR` | String

---