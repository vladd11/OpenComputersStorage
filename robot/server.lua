serverIpPort = "http://192.168.1.163:44444"

local internet = require("internet")
local utils = require("utils")
local movement = require("movement")
require "update"

while true do
    os.sleep(5)
    -- HTTP command request
    handle = internet.request(serverIpPort)
    local result = handle()
    print(result)

    load(result)(movement, utils, update)
end