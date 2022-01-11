local internet = require("internet")
local fs = require("filesystem")
local text = require("text")
local component = require("component")
local sides = require("sides")
local robot = require("robot")

require "find"
require "update"
require "goTo"
require "goBack"

function splitLines(s)
    if s:sub(-1) ~= "\n" then
        s = s .. "\n"
    end
    return s:gmatch("(.-)\n")
end

function serializeTable(val, name, skipnewlines, depth)
    skipnewlines = skipnewlines or false
    depth = depth or 0

    local tmp = string.rep(" ", depth)

    if name then
        tmp = tmp .. name .. " = "
    end

    if type(val) == "table" then
        tmp = tmp .. "{" .. (not skipnewlines and "\n" or "")

        for k, v in pairs(val) do
            tmp = tmp .. serializeTable(v, k, skipnewlines, depth + 1) .. "," .. (not skipnewlines and "\n" or "")
        end

        tmp = tmp .. string.rep(" ", depth) .. "}"
    elseif type(val) == "number" then
        tmp = tmp .. tostring(val)
    elseif type(val) == "string" then
        tmp = tmp .. string.format("%q", val)
    elseif type(val) == "boolean" then
        tmp = tmp .. (val and "true" or "false")
    else
        tmp = tmp .. "\"[inserializeable datatype:" .. type(val) .. "]\""
    end

    return tmp
end

while true do
    -- HTTP command request
    handle = internet.request("http://192.168.1.163:44444/commands")
    local result = ""
    for chunk in handle do
        result = result .. chunk
    end

    local lines = splitLines(result)
    for line in lines do
        tokens = text.tokenize(line)
        if tokens[1] == "take" then
            local positionX, positionY, positionZ, side, slot, count, id = tonumber(tokens[2]), tonumber(tokens[3]), tonumber(tokens[4]), tonumber(tokens[5]), tonumber(tokens[6]), tonumber(tokens[7]), tokens[8]
            goTo(positionX, positionY, positionZ)

            local pre = side
            if side == sides.left then
                robot.turnLeft()
                side = 3
            elseif side == sides.right then
                robot.turnRight()
                side = 3
            elseif side == sides.back then
                robot.turnAround()
                side = 3
            end
            component.inventory_controller.suckFromSlot(side, slot, count)
            print(id, side, positionX, positionY, positionZ)
            update(id, pre, positionX, positionY, positionZ)
            if pre == sides.left then
                robot.turnRight()
            elseif pre == sides.right then
                robot.turnLeft()
            elseif pre == sides.back then
                robot.turnAround()
            end
        elseif tokens[1] == "sort" then
            local slots = {}
            for i = 1, robot.inventorySize() do
                if component.inventory_controller.getStackInInternalSlot(i) ~= nil then
                    table.insert(slots, i)
                end
            end
            local places = findPlaceToStore(#slots)

            for index, i in ipairs(places) do
                local positionX, positionY, positionZ = component.navigation.getPosition()

                local chest = i[1]
                local side = tonumber(chest[4])
                local pre = tonumber(chest[4])

                local prevChest = nil;
                if index > 1 then
                    prevChest = places[index - 1]
                end

                if (prevChest ~= nil and chest[1] ~= prevChest[1]) or #places == 1 then
                    update(prevChest[1][5], prevChest[1][4], math.floor(positionX), math.floor(positionY), math.floor(positionZ))
                end
                goTo(tonumber(chest[1]), tonumber(chest[2]), tonumber(chest[3]))

                if side == sides.left then
                    -- TODO: rewrite to function
                    robot.turnLeft()
                    side = 3
                elseif side == sides.right then
                    robot.turnRight()
                    side = 3
                elseif side == sides.back then
                    robot.turnAround()
                    side = 3
                end
                robot.select(slots[index])
                print(side, i[2])
                component.inventory_controller.dropIntoSlot(side, i[2])
                preChest = i[1]
                if pre == sides.left then
                    robot.turnRight()
                elseif pre == sides.right then
                    robot.turnLeft()
                elseif pre == sides.back then
                    robot.turnAround()
                end
            end
        elseif tokens[1] == "update" then
            local id, side, positionX, positionY, positionZ = tokens[2], tonumber(tokens[3]), tonumber(tokens[4]), tonumber(tokens[5]), tonumber(tokens[6])
            goTo(positionX, positionY, positionZ)
            update(id, side, positionX, positionY, positionZ)
        elseif tokens[1] == "request" then
            goBack()
        elseif tokens[1] == "list" then
            list = fs.list("/home/chests")
            local out = ""
            for i in list do
                local f = io.open("/home/chests/" .. tostring(i), "r")
                out = out .. "\n|\n" .. i .. "\n" .. text.trim(f:read("*all"))
            end

            handle = internet.request("http://192.168.1.163:44444", out)
            local result = ""
            for chunk in handle do
                result = result .. chunk
            end
        end
    end
    :: goto_break ::
end