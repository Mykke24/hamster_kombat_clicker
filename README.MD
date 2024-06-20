# Master Hamster Kombat Bot!

❌ This project is intended for testing purposes only. Please refrain from abusing it. ❌

✅ Tested and confirmed to work as of `21 Jun 2024`. ✅


#### ⭐ If you find this project useful, please consider giving it a star! ⭐

<hr>

## 📗 Features:
- Auto Clicker / Tapper -> taps automatically until no more taps are left
- Auto boost -> buys automatically the boost when it is available
- Auto upgrade -> buys most profitable cards based on maxProfitRatioPerHour
- Auto daily cipher -> extracts the daily cipher from the API and automatically sends the request for the combo
- Auto sleeper -> if there is nothing to do, and wakes up to either auto tap or buy upgrades etc

## 🗒️ Todos:
- Auto Daily Combo - automatically buy the daily combo cards for 5 Mio extra Coins

## 💻 How to get the Auth Token for the bot
1. Get [HTTP-ToolKit](https://httptoolkit.com/)
2. Get an Emulator like [Bluestacks 5](https://www.bluestacks.com/) and root it. 
(For bluestacks 5 use [this guide](https://kimlisoft.com/how-to-root-bluestacks-5/))
3. Make sure you have [python](https://www.python.org/downloads/) downloaded.
4. Download telegram and login over there on the emulator's instance.
6. Launch bluestacks 5 and go to the `menu > settings` and click on `Advanced` tab. 
You will see something like [this](https://raw.githubusercontent.com/fw-real/hamster-kombat-auto-upgrade/main/screenshots/adbss.png) if ADB is enabled, if not, enable it and get the IP:PORT from there.
7. Download [HTTP-Toolkit-Android](https://play.google.com/store/apps/details?id=tech.httptoolkit.android.v1&hl=en_IN) there.
8. Open a terminal to the `adb_files` folder and run `adb connect ip:port`.
9. Launch HTTP-Toolkit and scroll till you find the `Android via ADB` tab like in [this screenshot](https://raw.githubusercontent.com/fw-real/hamster-kombat-auto-upgrade/main/screenshots/httptoolkitss.png), click on it and then click on the blue option which contains the `ip:port`.
10. Go to view tab after that and you should be getting intercepted, for detailed documentation visit the [official guide on intercepting android with http-toolkit](https://httptoolkit.com/docs/guides/android/)
11. Once your interception is setup, you should see [this](https://raw.githubusercontent.com/fw-real/hamster-kombat-auto-upgrade/main/screenshots/interceptingss.png) when HTTP-Toolkit android is opened (it will usualy open itself but if it didn't just open and check) where both user and system trust are enabled, if everything went correct.
12. Launch telegram and then open hamster kombat game while you are being intercepted.
13. After you have successfully opened hamster kombat and you are in the game, go back to HTTP-Toolkit PC and press `ctrl+f` or search for `https://api.hamsterkombat.io/auth/me-telegram`. Once you find it, click on it and see the headers, one of them would be `Authorization`: `Bearer tokenhere` copy the part after the `Bearer` thats your authorization token, you are now done with intercepting, you can close everything as per your liking. 
Here's a [screenshot](https://raw.githubusercontent.com/fw-real/hamster-kombat-auto-upgrade/main/screenshots/req.png) so you get what I mean better on how to get the token.
14. if you have copied the Auth Token - run the bot and paste it when it asks you...
15. enjoy the bot

If you find this project useful and would like to support its development, you can buy me a coffee or a donut! Your support is greatly appreciated.

## 📑 Way to support me:
- use my [referral](https://t.me/haMster_kombat_bot/start?startapp=kentId177023377) link in Hamster Combat
