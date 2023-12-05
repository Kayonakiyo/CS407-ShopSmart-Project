/**
 * This code is meant to be run on a server and act as an endpoint that runs this
 * puppeteer webdriver. This makes it easier for the Java end of things in the mobile 
 * application to easily 'run' these scripts by sending POST requests to it and letting
 * this script do the work. Requires the packages listed below to work as well as likely
 * port forwarding the IP (or using a domain) to the internet to access.
 */
const express = require("express");
const multer = require("multer");
const puppeteer = require("puppeteer-extra");
const StealthPlugin = require('puppeteer-extra-plugin-stealth');
const UserAgent = require('user-agents');
const app = express();
const port = 3000;
const upload = multer();

var userAgent = new UserAgent();
puppeteer.use(StealthPlugin())
//app.use(express.urlencoded({extended:true}));

app.post("/", upload.none(), (req, res) => {
    const query = req.body['query']
    console.log(query)
    console.log(`Fetching query for ${query}... This may take a while...`)
    var jsonlist = []
    UWBS_Scrape(query)
    .then((result) => {
        res.json(result)
    }).then(console.log('Query Complete!'));

    
});

app.listen(port, () => {
    console.log(`Server listening on port ${port}`);
});

const UWBS_Scrape = async (query) => {
    const url = "https://www.uwbookstore.com/merchlist?searchtype=all&txtSearch=" + query.toString().trim();
    const browser = await puppeteer.launch({ headless: 'false'});
    const page = await browser.newPage();
    await page.setUserAgent(userAgent.random().toString())
    await page.setViewport({ width: 1920, height: 1080 });
    await page.goto(url);
    await page.waitForSelector('.merchColumn.col-12.grid.merch__card')
    await page.screenshot({ path: 'preparse1stpage.png' })
    var hasNextPage = true;
    var UWBSItemList = []
    while (hasNextPage) {
        const items = await page.evaluate(() => {
            console.log("current page data :" + document)

            const merchItemDivs = Array.from(document.querySelectorAll('.merchColumn.col-12.grid.merch__card .merchItem.col-6.merch__card-item'));

            var items = []
            merchItemDivs.forEach((item) => {
                let itemName = item.querySelector('.merchTitle').textContent
                let itemPrice = item.querySelector('.merchPriceCurrent').textContent
                itemPrice = itemPrice.substring(itemPrice.indexOf('$')+1);
                //let itemSKU = item.querySelector('.merchSKU').textContent.toString().trim()
                let itemImage = item.querySelector('.merchImage').src
                let itemLink = item.querySelector('.merchLink').href.toString().trim()
                itemLink = itemLink.substring(0, itemLink.indexOf('&'))
                items.push({ name: itemName, price: itemPrice, store: "UW Bookstore", onlineLink: itemLink, imageUrl: itemImage })
            });
            return items;
        });


        UWBSItemList = UWBSItemList.concat(items)
        var nextPageStatus = await page.$('.chevRight .hover_pointer')
        if(nextPageStatus){
            await page.evaluate(() => {
                document.querySelector('.chevRight .hover_pointer').click();
            });
        } else {
            await browser.close(); // clean up before returning data
            return UWBSItemList
        }

        // the most important part, wait for this! or the context might not update and be invalid upon next evaluation!
        await page.waitForSelector('.merchColumn.col-12.grid.merch__card')

    }
}

