local component = require("component")
local internet = require("internet")
local fs = require("filesystem")
local text = require("text")
local robot = require("robot")

local utils = {

}

function utils.list()
    chests = fs.list("/home/chests")
    local out = utils.internalList()
    for i in chests do
        local f = io.open("/home/chests/" .. tostring(i), "r")
        if f == nil then
            return
        end

        out = out .. "\n|\n" .. i .. "\n" .. text.trim(f:read("*all"))
        f:close()
    end

    internet.request(serverIpPort, out)();
end

function utils.internalList()
    local out = ""
    for i = 1, robot.inventorySize() do
        local item = component.inventory_controller.getStackInInternalSlot(i)
        if item ~= nil then
            out = out .. item.name .. " " .. item.size .. " " .. tostring(item.hasTag) .. " " .. item.label .. "\n"
        end
    end
    return out
end

return utils