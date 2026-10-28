# Distance Leveling (NeoForge 1.21.1)

มอนสเตอร์แข็งแกร่งขึ้นตามระยะห่างจากจุดเกิดของโลก (world spawn)
เป็นการเขียนใหม่โดยใช้แนวคิดเดียวกับม็อด *Auto Leveling* ของ Daripher (Forge 1.20.1)

## วิธี build

ต้องใช้ JDK 21 และ Gradle (หรือ copy Gradle wrapper จาก MDK ของ NeoForge 1.21.1 มาใส่)

```
gradle build
```

ไฟล์ม็อดจะอยู่ที่ `build/libs/distanceleveling-1.0.0.jar`
ทดลองรันเกมด้วย `gradle runClient` หรือ `gradle runServer`

> ก่อน build ให้ตรวจ 2 ค่าใน `build.gradle`:
> - เวอร์ชันของปลั๊กอิน `net.neoforged.moddev`
> - เวอร์ชัน NeoForge (`21.1.x` ล่าสุดจาก https://projects.neoforged.net/neoforged/neoforge)
>
> และแก้ `group`, `authors`, `modId`/package ให้เป็นของคุณ

## ฟีเจอร์

- เลเวลมอนตามระยะห่างจาก spawn (+ ตามความลึกใต้ระดับน้ำทะเล + สุ่มเพิ่ม)
- แสดงเลเวลเหนือหัวมอน (เมื่อเล็งหรือแสดงตลอด)
- เพิ่ม attribute ตามเลเวล (ค่าเริ่มต้น: เลือด +5%, ดาเมจ +3% ต่อเลเวล)
- EXP ที่ดรอปเพิ่มตามเลเวล
- blacklist / whitelist (รองรับ `modid:*`)
- ทำงานบน server ได้ (ฝั่ง client ใช้แสดงเลเวลเท่านั้น)

## Config

ไฟล์ `config/distanceleveling-common.toml` (สร้างอัตโนมัติหลังรันครั้งแรก)
ชื่อค่าเหมือนต้นฉบับ: `starting_level`, `max_level`, `levels_per_distance`,
`levels_per_deepness`, `random_level_bonus`, `exp_bonus_per_level`,
`attribute_bonuses`, `blacklist`, `whitelist`, `always_show_level`, `show_level_when_looking_at`

รูปแบบ `attribute_bonuses`: `"attribute_id=ค่าต่อเลเวล"` เช่น `"minecraft:generic.max_health=0.05"` (+5% ของค่าฐานต่อเลเวล)
หมายเหตุ: ใน 1.21.1 ชื่อ attribute ยังมีคำว่า `generic.` นำหน้า

## ที่ยังไม่ได้ทำ (เทียบกับต้นฉบับ)

- คำสั่ง `/autoleveling addlevel`
- ไอเท็ม blacklist/whitelist tool
- loot condition `level_check`, ตาราง equipment ตามเลเวล, เท็กซ์เจอร์ตามเลเวล
- ตั้งค่าแยกตามมิติ/ตามมอนผ่าน data pack
- รองรับ Jade

## ใบอนุญาต

GPL-3.0-or-later (ให้สอดคล้องกับต้นฉบับ) — ใส่ไฟล์ LICENSE ฉบับเต็มก่อนเผยแพร่
